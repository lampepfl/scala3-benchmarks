import { memo, useMemo } from "react";
import createPlotlyComponent from "react-plotly.js/factory";
import Plotly from "plotly.js-dist-min";
import type { RawMeasurements } from "../types";
import { plotlyColors } from "../plotlyTheme";
import { useIsMobile } from "../hooks/useIsMobile";
import { farOutFences } from "../outliers";

const Plot = createPlotlyComponent(Plotly);

interface ComparisonChartProps {
  /** version → (benchmark → measurements) */
  versionData: Map<string, RawMeasurements>;
  /** Ordered list of versions (first is the reference) */
  versions: string[];
  /** Clip the y axis to exclude extreme outliers. */
  hideOutliers: boolean;
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
// Plotly 4 shows an "Upload to Cloud" modebar button by default.
const PLOT_CONFIG = { responsive: true, showSendToCloud: false } as const;

export default memo(function ComparisonChart({
  versionData,
  versions,
  hideOutliers,
  colorMode,
}: ComparisonChartProps) {
  const isMobile = useIsMobile();
  const { traces, yClip } = useMemo(() => {
    // Outliers are detected per (version, benchmark) box; the y axis can then
    // be clipped to the range of all other points.
    let inlierMin = Infinity;
    let inlierMax = -Infinity;

    const refData = versionData.get(versions[0]);
    if (!refData) return { traces: [], yClip: null };

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
        const [lo, hi] = farOutFences(normalized);
        for (const v of normalized) {
          if (v >= lo && v <= hi) {
            inlierMin = Math.min(inlierMin, v);
            inlierMax = Math.max(inlierMax, v);
          }
        }
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
    if (inlierMin > inlierMax) return { traces, yClip: null };
    const pad = Math.max((inlierMax - inlierMin) * 0.05, 0.01);
    const min = inlierMin - pad;
    const max = inlierMax + pad;
    // A point outside its own box's fences can still lie within the range of
    // other boxes, so only count the points the clipped axis actually hides.
    const hidden = traces.reduce(
      (n, t) => n + t.y.filter((v: number) => v < min || v > max).length,
      0,
    );
    return { traces, yClip: hidden > 0 ? { min, max, hidden } : null };
  }, [versionData, versions]);

  const layout = useMemo(() => {
    const { font, grid, background, colorway } = plotlyColors(colorMode);

    // Plotly's autorange always covers every point, whatever `boxpoints` is,
    // so hiding outliers means clipping the autorange to the other points.
    // Clipping (rather than a fixed range) also applies to Autoscale and
    // double-click resets.
    const clip = hideOutliers ? yClip : null;
    const yTitle = "Relative to first version";

    return {
      paper_bgcolor: background,
      plot_bgcolor: background,
      font: { color: font },
      colorway,
      yaxis: {
        title: {
          text: clip
            ? `${yTitle} (${clip.hidden} outlier${clip.hidden === 1 ? "" : "s"} hidden)`
            : yTitle,
        },
        autorangeoptions: clip
          ? { clipmin: clip.min, clipmax: clip.max }
          : undefined,
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
        ? { t: 60, b: 120, l: 40, r: 20 }
        : { t: 60, b: 100, l: 60, r: 30 },
      dragmode: !isMobile && ("zoom" as const),
    };
  }, [colorMode, isMobile, hideOutliers, yClip]);

  return (
    <Plot
      data={traces}
      layout={layout}
      useResizeHandler
      style={PLOT_STYLE}
      config={isMobile ? { ...PLOT_CONFIG, scrollZoom: false, displayModeBar: false } : PLOT_CONFIG}
    />
  );
});
