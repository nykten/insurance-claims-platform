#!/usr/bin/env sh
set -eu
ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
if [ ! -f "$ROOT/openapi.json" ]; then
  curl --fail --silent http://localhost:8080/v3/api-docs -o "$ROOT/openapi.json"
fi
npx ng-openapi-gen --input "$ROOT/openapi.json" --output "$ROOT/src/app/api/generated"
