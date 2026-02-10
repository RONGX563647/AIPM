#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

# Load .env if present
if [ -f .env ]; then
  echo "Loading environment from .env"
  set -a
  # shellcheck disable=SC1090
  source .env
  set +a
fi

JAR=target/backend-1.0-SNAPSHOT.jar
LOG=backend-start.log
PID=backend.pid

if [ ! -f "$JAR" ]; then
  echo "Jar not found: $JAR. Build first with 'mvn -DskipTests package'"
  exit 1
fi

nohup java -jar "$JAR" > "$LOG" 2>&1 &
echo $! > "$PID"
echo "Started backend (PID $(cat $PID)), logs: $LOG"