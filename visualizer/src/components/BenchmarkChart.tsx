import { memo, useMemo, useCallback } from "react";
import createPlotlyComponent from "react-plotly.js/factory";
import Plotly from "plotly.js-dist-min";
import type { AggregatedRow, Config } from "../types";
import { plotlyColors } from "../plotlyTheme";
import { useIsMobile } from "../hooks/useIsMobile";
import { extractDate } from "../dates";

const Plot = createPlotlyComponent(Plotly);

interface BenchmarkChartProps {
  title: string;
  data: AggregatedRow[];
  /** First date (YYYY-MM-DD) to display; earlier rows still feed the moving average. */
  startDate: string;
  config: Config;
  colorMode: "day" | "night";
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

/** Extract commit hash from nightly version like "3.8.3-RC1-bin-20260206-7d9042f-NIGHTLY" */
function extractCommitHash(version: string): string | null {
  const match = version.match(/-([0-9a-f]{7,})-NIGHTLY$/i);
  return match ? match[1] : null;
}

const DAY_MS = 24 * 60 * 60 * 1000;

const PLOT_STYLE = { width: "100%", height: 400 } as const;
const PLOT_CONFIG = { responsive: true } as const;

export default memo(function BenchmarkChart({
  title,
  data,
  startDate,
  config,
  colorMode,
}: BenchmarkChartProps) {
  const isMobile = useIsMobile();
  const sorted = useMemo(
    () => [...data].sort((a, b) => extractDate(a.version).localeCompare(extractDate(b.version))),
    [data],
  );
  // Computed over the full history, so that it does not restart at the
  // beginning of the displayed period.
  const movingAvgs = useMemo(
    () => computeMovingAverage(sorted.map((r) => r.avg)),
    [sorted],
  );
  const firstShown = useMemo(() => {
    const i = sorted.findIndex((r) => extractDate(r.version) >= startDate);
    return i === -1 ? sorted.length : i;
  }, [sorted, startDate]);
  const shown = useMemo(() => sorted.slice(firstShown), [sorted, firstShown]);
  const shownMovingAvgs = useMemo(
    () => movingAvgs.slice(firstShown),
    [movingAvgs, firstShown],
  );
  const versions = useMemo(() => shown.map((r) => r.version), [shown]);
  const dates = useMemo(() => shown.map((r) => extractDate(r.version)), [shown]);
  const avgs = useMemo(() => shown.map((r) => r.avg), [shown]);

  // Plotly pads the x autorange by about 5% on each side for traces with
  // markers or error bars. Clip it to the data plus a small margin, so that
  // the points at both ends stay fully visible. Clipping (rather than a fixed
  // range) also applies to the Autoscale button and double-click resets.
  const xAutorangeOptions = useMemo(() => {
    const first = Date.parse(dates[0]);
    const last = Date.parse(dates[dates.length - 1]);
    if (!Number.isFinite(first) || !Number.isFinite(last)) return undefined;
    const pad = Math.max((last - first) * 0.01, DAY_MS);
    return { clipmin: first - pad, clipmax: last + pad };
  }, [dates]);

  const traces = useMemo(() => {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const scatterTrace: any = {
      x: dates,
      y: avgs,
      customdata: shown.map((r) => [r.version, r.count]),
      hovertemplate: "<b>%{customdata[0]}</b><br>avg: %{y}<br>count: %{customdata[1]}<extra></extra>",
      mode: "markers",
      type: "scatter",
      name: "Average",
    };

    if (config.errorBars) {
      scatterTrace.error_y = {
        type: "data",
        array: shown.map((r) => r.max - r.avg),
        arrayminus: shown.map((r) => r.avg - r.min),
        visible: true,
      };
    }

    const result: Plotly.PlotData[] = [scatterTrace];

    if (config.movingAverage) {
      result.push({
        x: dates,
        y: shownMovingAvgs,
        mode: "lines" as const,
        type: "scatter" as const,
        name: "Moving average",
      } as Plotly.PlotData);
    }

    return result;
  }, [shown, dates, avgs, shownMovingAvgs, config.errorBars, config.movingAverage]);

  const layout = useMemo(() => {
    const colors = plotlyColors(colorMode);
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
      title: undefined,
      paper_bgcolor: colors.background,
      plot_bgcolor: colors.background,
      font: { color: colors.font },
      colorway: colors.colorway,
      xaxis: {
        title: { text: "Version" },
        type: "date" as const,
        tickformat: "%b %d, %Y",
        autorangeoptions: xAutorangeOptions,
        zeroline: false,
        automargin: false,
        gridcolor: colors.grid,
        linecolor: colors.grid,
        tickcolor: colors.grid,
      },
      yaxis: {
        title: { text: yAxisTitle },
        zeroline: true,
        automargin: false,
        rangemode: config.yAxisAtZero ? ("tozero" as const) : undefined,
        gridcolor: colors.grid,
        zerolinecolor: colors.grid,
        linecolor: colors.grid,
        tickcolor: colors.grid,
      },
      legend: {
        orientation: "h" as const,
        yanchor: "bottom" as const,
        y: 1.02,
        xanchor: "right" as const,
        x: 1,
      },
      margin: isMobile
        ? { t: 30, b: 80, l: 40, r: 20 }
        : { t: 30, b: 60, l: 60, r: 30 },
      dragmode: !isMobile && ("zoom" as const),
    };
  }, [title, config.metric, config.yAxisAtZero, colorMode, isMobile, xAutorangeOptions]);

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
      config={isMobile ? { ...PLOT_CONFIG, scrollZoom: false, displayModeBar: false } : PLOT_CONFIG}
      onClick={handleClick}
    />
  );
});
