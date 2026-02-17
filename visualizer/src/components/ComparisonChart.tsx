import { memo, useMemo } from "react";
import createPlotlyComponent from "react-plotly.js/factory";
import Plotly from "plotly.js-dist-min";
import type { RawMeasurements } from "../types";
import { plotlyColors } from "../plotlyTheme";
import { useIsMobile } from "../hooks/useIsMobile";

const Plot = createPlotlyComponent(Plotly);

interface ComparisonChartProps {
  suiteName: string;
  /** version → (benchmark → measurements) */
  versionData: Map<string, RawMeasurements>;
  /** Ordered list of versions (first is the reference) */
  versions: string[];
  colorMode: "day" | "night";
}

function median(arr: number[]): number {
  const sorted = [...arr].sort((a, b) => a - b);
  const mid = Math.floor(sorted.length / 2);
  return sorted.length % 2 !== 0
    ? sorted[mid]
    : (sorted[mid - 1] + sorted[mid]) / 2;
}

const PLOT_STYLE = { width: "100%", height: 500 } as const;
const PLOT_CONFIG = { responsive: true } as const;

export default memo(function ComparisonChart({
  suiteName,
  versionData,
  versions,
  colorMode,
}: ComparisonChartProps) {
  const isMobile = useIsMobile();
  const traces = useMemo(() => {
    const refData = versionData.get(versions[0]);
    if (!refData) return [];

    // Compute reference medians
    const refMedians = new Map<string, number>();
    for (const [bench, times] of refData) {
      refMedians.set(bench, median(times));
    }

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const traces: any[] = [];
    for (const version of versions) {
      const data = versionData.get(version);
      if (!data) continue;

      let x: string[] = [];
      let y: number[] = [];

      const sortedBenchmarks = [...data.keys()].sort();
      for (const bench of sortedBenchmarks) {
        const refMedian = refMedians.get(bench);
        if (!refMedian) continue;
        const times = data.get(bench) ?? [];
        const normalized = times.map((t) => t / refMedian);
        x = x.concat(Array(normalized.length).fill(bench));
        y = y.concat(normalized);
      }

      traces.push({
        x,
        y,
        type: "box",
        name: version,
        jitter: 0.8,
        pointpos: 0,
        boxpoints: "all",
        boxvisible: false,
      });
    }
    return traces;
  }, [versionData, versions]);

  const layout = useMemo(() => {
    const { font, grid, background, colorway } = plotlyColors(colorMode);

    return {
      title: { text: suiteName },
      paper_bgcolor: background,
      plot_bgcolor: background,
      font: { color: font },
      colorway,
      yaxis: {
        title: { text: "Relative to first version" },
        zeroline: false,
        gridcolor: grid,
        linecolor: grid,
        tickcolor: grid,
      },
      xaxis: {
        gridcolor: grid,
        linecolor: grid,
        tickcolor: grid,
      },
      shapes: [
        {
          type: "line" as const,
          x0: 0,
          x1: 1,
          y0: 1,
          y1: 1,
          xref: "paper" as const,
          yref: "y" as const,
          line: { color: font, width: 1 },
        },
      ],
      boxmode: "group" as const,
      legend: {
        orientation: "h" as const,
        yanchor: "bottom" as const,
        y: 1.02,
        xanchor: "right" as const,
        x: 1,
      },
      margin: isMobile
        ? { t: 120, b: 120, l: 40, r: 20 }
        : { t: 120, b: 100, l: 60, r: 30 },
    };
  }, [suiteName, colorMode, isMobile]);

  return (
    <Plot
      data={traces}
      layout={layout}
      useResizeHandler
      style={PLOT_STYLE}
      config={PLOT_CONFIG}
    />
  );
});
