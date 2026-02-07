import "@primer/primitives/dist/css/functional/themes/light.css";
import "@primer/primitives/dist/css/primitives.css";
import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import App from "./App";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <App />
  </StrictMode>
);
