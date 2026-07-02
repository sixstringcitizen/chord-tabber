#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT"

if ! command -v mvn >/dev/null 2>&1; then
  echo "Maven not found. Install Maven and try again." >&2
  exit 1
fi

if ! command -v java >/dev/null 2>&1; then
  echo "Java not found. Install Java 21+ and try again." >&2
  exit 1
fi

echo "Building Chord Tabber browser bundle (mvn -Pbrowser package)..."
mvn -Pbrowser package

echo ""
echo "Done. Preview with:"
echo "  cd \"$ROOT\""
echo "  python3 -m http.server 8080"
echo "  open http://localhost:8080/"
