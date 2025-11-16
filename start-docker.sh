#!/bin/bash

# Script para iniciar Docker Compose y mantenerlo corriendo
# Uso: ./start-docker.sh

echo "🐳 Iniciando Eventia Core API con Docker Compose"
echo "================================================"
echo ""

# Verificar que Docker esté corriendo
if ! docker info > /dev/null 2>&1; then
    echo "❌ ERROR: Docker no está corriendo"
    echo "Por favor inicia Docker Desktop y vuelve a intentar"
    exit 1
fi

echo "✅ Docker está corriendo"
echo ""

# Detener contenedores previos si existen
echo "🧹 Limpiando contenedores previos..."
docker-compose down 2>/dev/null

echo ""
echo "🚀 Construyendo y iniciando servicios..."
echo "Esto puede tardar 5-10 minutos la primera vez"
echo ""
echo "Servicios que se iniciarán:"
echo "  - PostgreSQL (puerto 5432)"
echo "  - Redis (puerto 6379)"
echo "  - Redis Insight (puerto 5540)"
echo "  - Backend API (puerto 8080)"
echo ""
echo "⚠️  NO CIERRES ESTA VENTANA - Los servicios se detendrán"
echo "Para detener: presiona Ctrl+C"
echo ""
echo "================================================"
echo ""

# Iniciar Docker Compose
docker-compose up --build

# Este código solo se ejecuta si se detiene con Ctrl+C
echo ""
echo "🛑 Deteniendo servicios..."
docker-compose down
echo "✅ Servicios detenidos"

