#!/bin/bash
set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$PROJECT_DIR"

echo "== Reanudando servicios =="
SERVICES=("eventia-postgres" "eventia-redis" "eventia-backend" "eventia-redis-insight")
for s in "${SERVICES[@]}"; do
  if docker ps -a --format '{{.Names}}' | grep -qx "$s"; then
    echo "Reanudando $s (si está pausado)"
    docker unpause "$s" 2>/dev/null || true
  fi
done

echo "\n== Estado actual =="
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

echo "\n💡 Si algún servicio no estaba iniciado, puedes usar: docker-compose up -d"

