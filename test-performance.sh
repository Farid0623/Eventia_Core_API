#!/bin/bash

# Script de prueba de rendimiento simple con Apache Bench (ab)
# Uso: ./test-performance.sh

API_URL="http://localhost:8080"
REQUESTS=100
CONCURRENCY=10

echo "🚀 Pruebas de Rendimiento - Eventia Core API"
echo "============================================="
echo ""

# Verificar que la API esté corriendo
if ! curl -s "${API_URL}/actuator/health" > /dev/null; then
    echo "❌ La API no está corriendo en ${API_URL}"
    echo ""
    echo "Para iniciarla:"
    echo "  ./gradlew bootRun"
    exit 1
fi

echo "✅ API está corriendo"
echo ""

# Verificar si ab está instalado
if ! command -v ab &> /dev/null; then
    echo "⚠️  Apache Bench (ab) no está instalado"
    echo ""
    echo "Para instalar:"
    echo "  macOS: ya viene instalado"
    echo "  Linux: sudo apt-get install apache2-utils"
    exit 1
fi

echo "📊 Configuración:"
echo "  - Requests totales: ${REQUESTS}"
echo "  - Concurrencia: ${CONCURRENCY}"
echo "  - URL base: ${API_URL}"
echo ""

# Test 1: GET /api/v1/eventos
echo "🔹 Test 1: GET /api/v1/eventos"
echo "   (Listar todos los eventos)"
ab -n ${REQUESTS} -c ${CONCURRENCY} \
   -H "Accept: application/json" \
   "${API_URL}/api/v1/eventos" 2>&1 | grep -E "Requests per second|Time per request|Transfer rate|Failed requests"

echo ""

# Test 2: GET /api/v1/participantes
echo "🔹 Test 2: GET /api/v1/participantes"
echo "   (Listar todos los participantes)"
ab -n ${REQUESTS} -c ${CONCURRENCY} \
   -H "Accept: application/json" \
   "${API_URL}/api/v1/participantes" 2>&1 | grep -E "Requests per second|Time per request|Transfer rate|Failed requests"

echo ""

# Test 3: Health check
echo "🔹 Test 3: GET /actuator/health"
echo "   (Health check - debe ser muy rápido)"
ab -n ${REQUESTS} -c ${CONCURRENCY} \
   "${API_URL}/actuator/health" 2>&1 | grep -E "Requests per second|Time per request|Transfer rate|Failed requests"

echo ""
echo "✅ Pruebas de rendimiento completadas"
echo ""
echo "💡 Para pruebas más avanzadas:"
echo "  - K6: k6 run performance-tests/k6-load-test.js"
echo "  - JMeter: jmeter -t performance-tests/eventia-load-test.jmx"
echo "  - Métricas en vivo: curl ${API_URL}/actuator/metrics"
echo ""
echo "📊 Métricas recomendadas:"
echo "  - Requests/sec: >50 ✅"
echo "  - Time/request: <100ms ✅"
echo "  - Failed requests: 0 ✅"

