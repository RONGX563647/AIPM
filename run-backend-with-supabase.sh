#!/usr/bin/env bash
set -e
# Usage: copy backend/.env.example -> backend/.env and fill values,
# then run this script from the repo root to start the backend with those env vars.

ENVFILE="backend/.env"
if [ ! -f "$ENVFILE" ]; then
  echo "No $ENVFILE found. Copying example to $ENVFILE and please edit it with real credentials."
  cp backend/.env.example "$ENVFILE"
  exit 0
fi

# Export variables from .env
export $(grep -v '^#' "$ENVFILE" | xargs)

echo "Starting backend... (cd backend && mvn spring-boot:run)"
cd backend
mvn spring-boot:run
