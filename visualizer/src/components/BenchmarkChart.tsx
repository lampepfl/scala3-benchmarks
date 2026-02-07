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

export default function BenchmarkChart({
  title,
  data,
  config,
}: BenchmarkChartProps) {
  const versions = data.map((r) => r.version);
  const avgs = data.map((r) => r.avg);
  const indices = data.map((_, i) => i);

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const scatterTrace: any = {
    x: indices,
    y: avgs,
    customdata: versions,
    hovertemplate: "<b>%{customdata}</b><br>avg: %{y}<extra></extra>",
    mode: "markers",
    type: "scatter",
    name: "Average",
  };

  if (config.errorBars) {
    scatterTrace.error_y = {
      type: "data",
      array: data.map((r) => r.max - r.avg),
      arrayminus: data.map((r) => r.avg - r.min),
      visible: true,
    };
  }

  const traces: Plotly.PlotData[] = [scatterTrace];

  if (config.movingAverage) {
    traces.push({
      x: indices,
      y: computeMovingAverage(avgs),
      mode: "lines" as const,
      type: "scatter" as const,
      name: "Moving average",
    } as Plotly.PlotData);
  }

  const tickInterval = Math.max(1, Math.ceil(indices.length / 5));
  const tickIndices = indices.filter((_, i) => i % tickInterval === 0);
  const tickLabels = tickIndices.map((i) => extractDate(versions[i]));

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

  const layout: Partial<Plotly.Layout> = {
    title: { text: title },
    xaxis: {
      title: { text: "Version" },
      tickvals: tickIndices,
      ticktext: tickLabels,
      zeroline: false,
    },
    yaxis: {
      title: { text: yAxisTitle },
      zeroline: true,
      range: config.yAxisAtZero
        ? [0, Math.max(...avgs) * 1.1]
        : undefined,
    },
    legend: {
      orientation: "h",
      yanchor: "bottom",
      y: 1.02,
      xanchor: "right",
      x: 1,
    },
    margin: { t: 40, b: 60 },
  };

  const handleClick = (event: Plotly.PlotMouseEvent) => {
    const point = event.points[0];
    if (!point) return;
    const version = versions[point.pointIndex];
    const hash = extractCommitHash(version);
    if (hash) {
      window.open(`https://github.com/scala/scala3/commit/${hash}`, "_blank");
    }
  };

  return (
    <Plot
      data={traces}
      layout={layout}
      useResizeHandler
      style={{ width: "100%", height: 400 }}
      config={{ responsive: true }}
      onClick={handleClick}
    />
  );
}
