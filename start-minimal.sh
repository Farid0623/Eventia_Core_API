#!/bin/bash

# Script simplificado - Solo servicios ESENCIALES
# Uso: ./start-minimal.sh

echo "🚀 Iniciando Eventia Core API - SERVICIOS ESENCIALES"
echo "===================================================="
echo ""

# Verificar Docker
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker no está corriendo. Inicia Docker Desktop."
    exit 1
fi

echo "✅ Docker está corriendo"
echo ""

# Limpiar contenedores previos
echo "🧹 Limpiando contenedores previos..."
docker-compose down 2>/dev/null

echo ""
echo "🚀 Iniciando servicios esenciales:"
echo "  1. PostgreSQL (Base de datos)"
echo "  2. Redis (Caché)"
echo "  3. Backend (Tu API)"
echo ""
echo "⏱️  Tiempo estimado: 7-10 minutos la primera vez"
echo "⚠️  NO CIERRES ESTA VENTANA"
echo ""

# Iniciar solo los servicios esenciales
docker-compose up postgres redis backend

echo ""
echo "🛑 Servicios detenidos"
echo "Para detener completamente: docker-compose down"

