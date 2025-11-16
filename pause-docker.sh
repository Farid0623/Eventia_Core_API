#!/bin/bash
set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$PROJECT_DIR"

echo "== Verificando contenedores activos =="
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

# Primero intentamos pausar por docker-compose (proyecto actual)
echo "\n== Intentando pausar servicios con docker-compose =="
if docker-compose ps >/dev/null 2>&1; then
  docker-compose pause || true
else
  echo "docker-compose no está disponible o no es un proyecto válido aquí."
fi

# Pausa específica por nombre (idempotente)
SERVICES=("eventia-backend" "eventia-postgres" "eventia-redis" "eventia-redis-insight")

echo "\n== Pausando contenedores específicos si están activos =="
for s in "${SERVICES[@]}"; do
  if docker ps --format '{{.Names}}' | grep -qx "$s"; then
    echo "Pausando $s"
    docker pause "$s" || true
  else
    echo "$s no está corriendo"
  fi
done

echo "\n== Estado después de pausar =="
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

echo "\n✅ Pausa ejecutada (si había servicios activos)"

