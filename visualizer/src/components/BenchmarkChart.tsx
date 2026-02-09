import { memo, useMemo, useCallback } from "react";
import createPlotlyComponent from "react-plotly.js/factory";
import Plotly from "plotly.js-dist-min";
import type { AggregatedRow, Config } from "../types";

const Plot = createPlotlyComponent(Plotly);

interface BenchmarkChartProps {
  title: string;
  data: AggregatedRow[];
  config: Config;
}

function computeMovingAverage(values: number[], windowSize = 10): number[] {
  return values.map((_, i) => {
    const start = Math.max(0, i - windowSize + 1);
    let sum = 0;
    for (let j = start; j <= i; j++) {
      sum += values[j];
    }
    return sum / (i - start + 1);
  });
}

function extractDate(version: string): string {
  const match = version.match(/(\d{8})/);
  if (!match) return version.slice(0, 10);
  const d = match[1];
  return `${d.slice(0, 4)}-${d.slice(4, 6)}-${d.slice(6, 8)}`;
}

/** Extract commit hash from nightly version like "3.8.3-RC1-bin-20260206-7d9042f-NIGHTLY" */
function extractCommitHash(version: string): string | null {
  const match = version.match(/-([0-9a-f]{7,})-NIGHTLY$/i);
  return match ? match[1] : null;
}

const PLOT_STYLE = { width: "100%", height: 400 } as const;
const PLOT_CONFIG = { responsive: true } as const;

export default memo(function BenchmarkChart({
  title,
  data,
  config,
}: BenchmarkChartProps) {
  const sorted = useMemo(
    () => [...data].sort((a, b) => extractDate(a.version).localeCompare(extractDate(b.version))),
    [data],
  );
  const versions = useMemo(() => sorted.map((r) => r.version), [sorted]);
  const dates = useMemo(() => sorted.map((r) => extractDate(r.version)), [sorted]);
  const avgs = useMemo(() => sorted.map((r) => r.avg), [sorted]);

  const traces = useMemo(() => {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const scatterTrace: any = {
      x: dates,
      y: avgs,
      customdata: sorted.map((r) => [r.version, r.count]),
      hovertemplate: "<b>%{customdata[0]}</b><br>avg: %{y}<br>count: %{customdata[1]}<extra></extra>",
      mode: "markers",
      type: "scatter",
      name: "Average",
    };

    if (config.errorBars) {
      scatterTrace.error_y = {
        type: "data",
        array: sorted.map((r) => r.max - r.avg),
        arrayminus: sorted.map((r) => r.avg - r.min),
        visible: true,
      };
    }

    const result: Plotly.PlotData[] = [scatterTrace];

    if (config.movingAverage) {
      result.push({
        x: dates,
        y: computeMovingAverage(avgs),
        mode: "lines" as const,
        type: "scatter" as const,
        name: "Moving average",
      } as Plotly.PlotData);
    }

    return result;
  }, [sorted, dates, avgs, versions, config.errorBars, config.movingAverage]);

  const layout = useMemo(() => {
    const yAxisTitle =
      config.metric === "time"
        ? "Time (ms)"
        : config.metric === "allocs"
          ? "Allocations (MB)"
          : config.metric === "gc"
            ? "GC events"
            : config.metric === "comp"
              ? "JIT time (ms)"
              : config.metric;

    return {
      title: { text: title },
      xaxis: {
        title: { text: "Version" },
        type: "date" as const,
        tickformat: "%b %d, %Y",
        zeroline: false,
        automargin: false,
      },
      yaxis: {
        title: { text: yAxisTitle },
        zeroline: true,
        automargin: false,
        rangemode: config.yAxisAtZero ? ("tozero" as const) : undefined,
      },
      legend: {
        orientation: "h" as const,
        yanchor: "bottom" as const,
        y: 1.02,
        xanchor: "right" as const,
        x: 1,
      },
      margin: { t: 40, b: 60, l: 60, r: 30 },
    };
  }, [title, config.metric, config.yAxisAtZero]);

  const handleClick = useCallback(
    (event: Plotly.PlotMouseEvent) => {
      const point = event.points[0];
      if (!point) return;
      const version = versions[point.pointIndex];
      const hash = extractCommitHash(version);
      if (hash) {
        window.open(
          `https://github.com/scala/scala3/commit/${hash}`,
          "_blank",
        );
      }
    },
    [versions],
  );

  return (
    <Plot
      data={traces}
      layout={layout}
      useResizeHandler
      style={PLOT_STYLE}
      config={PLOT_CONFIG}
      onClick={handleClick}
    />
  );
});
