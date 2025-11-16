#!/bin/bash

# Script para facilitar el desarrollo de Eventia Core API
# Asegura que se use Java 21 para todas las operaciones

export JAVA_HOME=$(/usr/libexec/java_home -v 21)
export PATH=$JAVA_HOME/bin:$PATH

echo "🚀 Eventia Core API - Script de Desarrollo"
echo "============================================"
echo "Java version: $(java -version 2>&1 | head -n 1)"
echo ""

case "$1" in
  build)
    echo "📦 Compilando proyecto..."
    ./gradlew clean build -x test
    ;;
  test)
    echo "🧪 Ejecutando pruebas..."
    ./gradlew test
    ;;
  test-unit)
    echo "🧪 Ejecutando pruebas unitarias..."
    ./gradlew test --tests "*Test"
    ;;
  test-integration)
    echo "🧪 Ejecutando pruebas de integración..."
    ./gradlew test --tests "*IntegrationTest"
    ;;
  test-e2e)
    echo "🧪 Ejecutando pruebas end-to-end..."
    ./gradlew test --tests "*E2ETest"
    ;;
  check)
    echo "🔍 Ejecutando análisis de código..."
    ./gradlew check
    ;;
  checkstyle)
    echo "📝 Ejecutando Checkstyle..."
    ./gradlew checkstyleMain checkstyleTest
    ;;
  spotbugs)
    echo "🐛 Ejecutando SpotBugs..."
    ./gradlew spotbugsMain spotbugsTest
    ;;
  coverage)
    echo "📊 Generando reporte de cobertura..."
    ./gradlew jacocoTestReport
    echo "Reporte disponible en: build/reports/jacoco/test/html/index.html"
    ;;
  run)
    echo "▶️  Iniciando aplicación..."
    ./gradlew bootRun
    ;;
  jar)
    echo "📦 Creando JAR ejecutable..."
    ./gradlew bootJar
    echo "JAR creado en: build/libs/"
    ;;
  docker-up)
    echo "🐳 Iniciando servicios con Docker Compose..."
    docker-compose up -d
    ;;
  docker-down)
    echo "🐳 Deteniendo servicios Docker..."
    docker-compose down
    ;;
  docker-logs)
    echo "📋 Mostrando logs de Docker..."
    docker-compose logs -f backend
    ;;
  clean)
    echo "🧹 Limpiando proyecto..."
    ./gradlew clean
    ;;
  *)
    echo "Uso: ./dev.sh [comando]"
    echo ""
    echo "Comandos disponibles:"
    echo "  build              - Compilar el proyecto"
    echo "  test               - Ejecutar todas las pruebas"
    echo "  test-unit          - Ejecutar solo pruebas unitarias"
    echo "  test-integration   - Ejecutar solo pruebas de integración"
    echo "  test-e2e           - Ejecutar solo pruebas end-to-end"
    echo "  check              - Ejecutar análisis de código"
    echo "  checkstyle         - Ejecutar Checkstyle"
    echo "  spotbugs           - Ejecutar SpotBugs"
    echo "  coverage           - Generar reporte de cobertura"
    echo "  run                - Iniciar la aplicación"
    echo "  jar                - Crear JAR ejecutable"
    echo "  docker-up          - Iniciar servicios Docker"
    echo "  docker-down        - Detener servicios Docker"
    echo "  docker-logs        - Ver logs de Docker"
    echo "  clean              - Limpiar archivos generados"
    echo ""
    exit 1
    ;;
esac

echo ""
echo "✅ Comando completado!"

