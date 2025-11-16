#!/bin/bash

# Script para verificar el estado de los servicios Docker
# Uso: ./check-status.sh

echo "🔍 Verificando estado de Eventia Core API"
echo "=========================================="
echo ""

# Verificar Docker
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker no está corriendo"
    exit 1
fi

echo "✅ Docker está corriendo"
echo ""

# Verificar contenedores
echo "📦 Contenedores:"
echo "----------------"
CONTAINERS=$(docker ps --format "{{.Names}}" | grep eventia)

if [ -z "$CONTAINERS" ]; then
    echo "❌ No hay contenedores de Eventia corriendo"
    echo ""
    echo "Para iniciar los servicios, ejecuta:"
    echo "  ./start-docker.sh"
    echo ""
    echo "O manualmente:"
    echo "  docker-compose up --build"
    exit 0
fi

# Mostrar estado de cada contenedor
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" | grep -E "NAMES|eventia"

echo ""
echo "🌐 URLs de Servicios:"
echo "--------------------"
echo "  API Backend:    http://localhost:8080"
echo "  PostgreSQL:     localhost:5432"
echo "  Redis:          localhost:6379"
echo "  Redis Insight:  http://localhost:5540"

echo ""
echo "🏥 Health Checks:"
echo "-----------------"

# Health check de la API
if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
    HEALTH=$(curl -s http://localhost:8080/actuator/health | grep -o '"status":"[^"]*"' | cut -d'"' -f4)
    if [ "$HEALTH" = "UP" ]; then
        echo "✅ API Backend: UP"
    else
        echo "⚠️  API Backend: $HEALTH"
    fi
else
    echo "❌ API Backend: No responde (puede estar iniciando)"
fi

# Health check de PostgreSQL
if docker exec eventia-postgres pg_isready -U eventia_user > /dev/null 2>&1; then
    echo "✅ PostgreSQL: UP"
else
    echo "❌ PostgreSQL: No disponible"
fi

# Health check de Redis
if docker exec eventia-redis redis-cli ping > /dev/null 2>&1; then
    echo "✅ Redis: UP"
else
    echo "❌ Redis: No disponible"
fi

echo ""
echo "📊 Para ver logs en tiempo real:"
echo "  docker-compose logs -f"
echo ""
echo "🛑 Para detener servicios:"
echo "  docker-compose down"
echo ""

