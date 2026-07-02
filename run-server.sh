#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT"

echo "Building dependencies..."
mvn -pl chord-tabber-server -am install -DskipTests

echo "Starting Chord Tabber server..."
mvn -pl chord-tabber-server exec:java -Dexec.cleanupDaemonThreads=false -Dchordtabber.web="$ROOT"
