#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
PID=backend.pid
if [ -f "$PID" ]; then
  PIDVAL=$(cat "$PID")
  echo "Stopping PID $PIDVAL"
  kill "$PIDVAL" || true
  rm -f "$PID"
  echo "Stopped"
else
  echo "No PID file ($PID) found. Nothing to stop."
fi