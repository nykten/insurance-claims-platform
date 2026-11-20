# CHX Claims frontend

# CHX Claims frontend

Run locally:

```bash
npm install
npm test
npm run build
npx ng serve --open
```

The dev server proxies `/api` to `http://localhost:8080`. Generate the typed API client after claims-service is running:

```bash
./scripts/generate-api.sh
```

The Docker image is served at `http://localhost:4200` and nginx proxies `/api/` to claims-service.
