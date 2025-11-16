#!/bin/bash

# Script para probar la API manualmente con cURL
# Uso: ./test-api.sh

API_URL="http://localhost:8080"

echo "🌐 Probando API REST - Eventia Core"
echo "===================================="
echo ""

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

test_endpoint() {
    local method=$1
    local endpoint=$2
    local description=$3
    local data=$4

    echo -e "${YELLOW}Probando: ${description}${NC}"
    echo "  ${method} ${endpoint}"

    if [ -z "$data" ]; then
        response=$(curl -s -w "\n%{http_code}" -X ${method} "${API_URL}${endpoint}")
    else
        response=$(curl -s -w "\n%{http_code}" -X ${method} \
            -H "Content-Type: application/json" \
            -d "${data}" \
            "${API_URL}${endpoint}")
    fi

    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')

    if [ "$http_code" -ge 200 ] && [ "$http_code" -lt 300 ]; then
        echo -e "  ${GREEN}✓ OK (${http_code})${NC}"
        echo "$body" | jq '.' 2>/dev/null || echo "$body"
    else
        echo -e "  ${RED}✗ FAIL (${http_code})${NC}"
        echo "$body" | jq '.' 2>/dev/null || echo "$body"
    fi
    echo ""
}

# Verificar si la API está corriendo
echo "1. Verificando que la API esté corriendo..."
if ! curl -s "${API_URL}/actuator/health" > /dev/null; then
    echo -e "${RED}❌ La API no está corriendo en ${API_URL}${NC}"
    echo ""
    echo "Para iniciarla:"
    echo "  - Con Docker: docker-compose up -d"
    echo "  - Local: ./gradlew bootRun"
    exit 1
fi

health=$(curl -s "${API_URL}/actuator/health")
status=$(echo "$health" | jq -r '.status' 2>/dev/null || echo "UNKNOWN")

if [ "$status" = "UP" ]; then
    echo -e "${GREEN}✓ API está UP${NC}"
else
    echo -e "${RED}✗ API está ${status}${NC}"
fi
echo ""

# Health check detallado
echo "2. Health Check"
test_endpoint "GET" "/actuator/health" "Health Check"

# Info de la aplicación
echo "3. Info de la Aplicación"
test_endpoint "GET" "/actuator/info" "Application Info"

# Crear un evento
echo "4. Crear un Evento"
evento_data='{
  "nombre": "Test Evento '$(date +%s)'",
  "descripcion": "Evento creado desde script de prueba",
  "fechaInicio": "2025-12-15T10:00:00",
  "fechaFin": "2025-12-15T18:00:00",
  "ubicacion": "Popayán",
  "capacidadMaxima": 50
}'
test_endpoint "POST" "/api/v1/eventos" "Crear evento" "$evento_data"

# Listar eventos
echo "5. Listar Todos los Eventos"
test_endpoint "GET" "/api/v1/eventos" "Listar eventos"

# Eventos disponibles
echo "6. Eventos con Cupos Disponibles"
test_endpoint "GET" "/api/v1/eventos/disponibles" "Eventos disponibles"

# Crear un participante
echo "7. Crear un Participante"
participante_data='{
  "nombre": "Test",
  "apellido": "Usuario",
  "email": "test'$(date +%s)'@example.com",
  "telefono": "3001234567",
  "documento": "'$(date +%s)'",
  "tipoDocumento": "CEDULA_CIUDADANIA"
}'
test_endpoint "POST" "/api/v1/participantes" "Crear participante" "$participante_data"

# Listar participantes
echo "8. Listar Todos los Participantes"
test_endpoint "GET" "/api/v1/participantes" "Listar participantes"

echo ""
echo -e "${GREEN}✅ Pruebas manuales completadas${NC}"
echo ""
echo "💡 Para ver más detalles:"
echo "  - Ver logs: docker-compose logs -f backend"
echo "  - Redis Insight: http://localhost:5540"
echo "  - Métricas: curl ${API_URL}/actuator/metrics"

