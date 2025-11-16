#!/bin/bash

# Script para ejecutar pruebas de forma fácil y rápida
# Uso: ./test-easy.sh [opcion]

export JAVA_HOME=$(/usr/libexec/java_home -v 21)

echo "🧪 Eventia Core API - Pruebas Fáciles"
echo "====================================="
echo ""

case "$1" in
  unit|unitarias)
    echo "▶️  Ejecutando pruebas UNITARIAS (rápidas, ~30 segundos)"
    ./gradlew test --tests "*Test" --info
    ;;

  integration|integracion)
    echo "▶️  Ejecutando pruebas de INTEGRACIÓN (~1 minuto)"
    ./gradlew test --tests "*IntegrationTest" --info
    ;;

  e2e|end-to-end)
    echo "▶️  Ejecutando pruebas END-TO-END (~2 minutos)"
    ./gradlew test --tests "*E2ETest" --info
    ;;

  all|todas)
    echo "▶️  Ejecutando TODAS las pruebas (~3 minutos)"
    ./gradlew test --info
    ;;

  quick|rapido)
    echo "▶️  Pruebas RÁPIDAS (solo unitarias, sin logs)"
    ./gradlew test --tests "*Test" -q
    ;;

  coverage|cobertura)
    echo "▶️  Ejecutando todas las pruebas + reporte de cobertura"
    ./gradlew test jacocoTestReport
    echo ""
    echo "📊 Abriendo reporte de cobertura..."
    open build/reports/jacoco/test/html/index.html || echo "Reporte en: build/reports/jacoco/test/html/index.html"
    ;;

  report|reporte)
    echo "▶️  Abriendo último reporte de pruebas"
    open build/reports/tests/test/index.html || echo "Reporte en: build/reports/tests/test/index.html"
    ;;

  watch|continuo)
    echo "▶️  Modo CONTINUO - Las pruebas se ejecutan al guardar cambios"
    echo "⚠️  Presiona Ctrl+C para detener"
    ./gradlew test --continuous
    ;;

  clean|limpiar)
    echo "🧹 Limpiando y ejecutando todas las pruebas desde cero"
    ./gradlew clean test
    ;;

  service|servicio)
    echo "▶️  Probando solo servicios de dominio"
    ./gradlew test --tests "*Service*Test" --info
    ;;

  controller|controlador)
    echo "▶️  Probando solo controladores REST"
    ./gradlew test --tests "*Controller*Test" --info
    ;;

  failed|fallidas)
    echo "▶️  Re-ejecutando solo las pruebas que FALLARON"
    ./gradlew test --rerun-tasks --tests "*Test"
    ;;

  *)
    echo "📖 Uso: ./test-easy.sh [opcion]"
    echo ""
    echo "Opciones disponibles:"
    echo ""
    echo "  🏃 Pruebas Rápidas:"
    echo "    quick          - Solo unitarias, sin logs (30 seg)"
    echo "    unit           - Pruebas unitarias (~30 seg)"
    echo "    integration    - Pruebas de integración (~1 min)"
    echo "    e2e            - Pruebas end-to-end (~2 min)"
    echo ""
    echo "  🎯 Pruebas Específicas:"
    echo "    service        - Solo servicios de dominio"
    echo "    controller     - Solo controladores REST"
    echo ""
    echo "  📊 Análisis:"
    echo "    all            - Todas las pruebas (~3 min)"
    echo "    coverage       - Todas + reporte de cobertura"
    echo "    report         - Ver último reporte HTML"
    echo ""
    echo "  🔧 Utilidades:"
    echo "    watch          - Modo continuo (auto-ejecuta al guardar)"
    echo "    clean          - Limpiar y ejecutar desde cero"
    echo "    failed         - Re-ejecutar solo las fallidas"
    echo ""
    echo "Ejemplos:"
    echo "  ./test-easy.sh quick        # Rápido para desarrollo"
    echo "  ./test-easy.sh unit         # Solo unitarias"
    echo "  ./test-easy.sh coverage     # Cobertura completa"
    echo "  ./test-easy.sh watch        # Auto-ejecuta al guardar"
    echo ""
    exit 1
    ;;
esac

echo ""
echo "✅ Pruebas completadas!"
echo ""
echo "💡 Tips:"
echo "  - Ver reporte HTML: ./test-easy.sh report"
echo "  - Ver cobertura: ./test-easy.sh coverage"
echo "  - Modo continuo: ./test-easy.sh watch"

