import { useState, useEffect, useCallback } from "react";
import type { ViewMode } from "../types";

export interface HashRoute {
  view: ViewMode;
  compareVersions: string[];
}

function parseHash(hash: string): HashRoute {
  if (hash.startsWith("#compare")) {
    const rest = hash.slice("#compare/".length);
    const versions = rest ? rest.split(",").filter(Boolean) : [];
    return { view: "compare", compareVersions: versions };
  }
  return { view: "timeseries", compareVersions: [] };
}

function buildHash(route: HashRoute): string {
  if (route.view === "compare") {
    if (route.compareVersions.length > 0) {
      return `#compare/${route.compareVersions.join(",")}`;
    }
    return "#compare";
  }
  return "";
}

export function useHashRouter(): [HashRoute, (route: HashRoute) => void] {
  const [route, setRouteState] = useState<HashRoute>(() =>
    parseHash(window.location.hash),
  );

  useEffect(() => {
    const handler = () => setRouteState(parseHash(window.location.hash));
    window.addEventListener("hashchange", handler);
    return () => window.removeEventListener("hashchange", handler);
  }, []);

  const setRoute = useCallback((newRoute: HashRoute) => {
    const hash = buildHash(newRoute);
    if (window.location.hash !== hash) {
      window.location.hash = hash;
    }
    setRouteState(newRoute);
  }, []);

  return [route, setRoute];
}
