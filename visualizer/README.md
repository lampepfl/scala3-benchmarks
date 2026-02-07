# Visualizer

Web app for visualizing Scala 3 compiler benchmark results. Built with React, [Primer React](https://primer.style/react), and [Plotly](https://plotly.com/javascript/).

## Development

```bash
npm install
npm run dev
```

## Build

```bash
npm run build
npm run preview   # preview the production build locally
```

## Deployment

The app is deployed to GitHub Pages automatically on pushes to `main` (see [`../.github/workflows/deploy-visualizer.yml`](../.github/workflows/deploy-visualizer.yml)). GitHub Pages must be configured to use "GitHub Actions" as the source in the repository settings.
