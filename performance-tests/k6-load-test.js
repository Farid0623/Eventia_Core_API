import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

// Métricas personalizadas
const errorRate = new Rate('errors');

// Configuración de la prueba
export let options = {
  stages: [
    { duration: '30s', target: 20 },   // Warm up: 20 usuarios
    { duration: '1m', target: 50 },    // Carga normal: 50 usuarios
    { duration: '30s', target: 100 },  // Pico de carga: 100 usuarios
    { duration: '1m', target: 50 },    // Bajar carga: 50 usuarios
    { duration: '30s', target: 0 },    // Cool down
  ],
  thresholds: {
    // 95% de las peticiones deben responder en menos de 500ms
    http_req_duration: ['p(95)<500'],
    // Tasa de error debe ser menor al 1%
    errors: ['rate<0.01'],
    // 95% de las peticiones exitosas
    'http_req_failed': ['rate<0.05'],
  },
};

const BASE_URL = 'http://localhost:8080';

export default function () {
  // Test 1: Health Check
  let healthRes = http.get(`${BASE_URL}/actuator/health`);
  check(healthRes, {
    'health check is 200': (r) => r.status === 200,
    'health status is UP': (r) => r.json('status') === 'UP',
  }) || errorRate.add(1);

  sleep(1);

  // Test 2: Listar eventos
  let eventosRes = http.get(`${BASE_URL}/api/v1/eventos`, {
    headers: { 'Accept': 'application/json' },
  });
  check(eventosRes, {
    'listar eventos is 200': (r) => r.status === 200,
    'eventos response is array': (r) => Array.isArray(r.json()),
  }) || errorRate.add(1);

  sleep(1);

  // Test 3: Listar eventos disponibles
  let disponiblesRes = http.get(`${BASE_URL}/api/v1/eventos/disponibles`);
  check(disponiblesRes, {
    'eventos disponibles is 200': (r) => r.status === 200,
  }) || errorRate.add(1);

  sleep(1);

  // Test 4: Crear evento (carga de escritura)
  let timestamp = Date.now();
  let eventoPayload = JSON.stringify({
    nombre: `Evento K6 ${timestamp}`,
    descripcion: 'Prueba de carga con K6',
    fechaInicio: '2025-12-15T10:00:00',
    fechaFin: '2025-12-15T18:00:00',
    ubicacion: 'Test Location',
    capacidadMaxima: 100
  });

  let crearEventoRes = http.post(
    `${BASE_URL}/api/v1/eventos`,
    eventoPayload,
    {
      headers: { 'Content-Type': 'application/json' },
    }
  );
  check(crearEventoRes, {
    'crear evento is 201': (r) => r.status === 201,
    'evento tiene id': (r) => r.json('id') != null,
    'evento está ACTIVO': (r) => r.json('estado') === 'ACTIVO',
  }) || errorRate.add(1);

  sleep(1);

  // Test 5: Listar participantes
  let participantesRes = http.get(`${BASE_URL}/api/v1/participantes`);
  check(participantesRes, {
    'listar participantes is 200': (r) => r.status === 200,
  }) || errorRate.add(1);

  sleep(2);
}

// Función que se ejecuta al final
export function handleSummary(data) {
  return {
    'performance-tests/summary.json': JSON.stringify(data),
    stdout: textSummary(data, { indent: ' ', enableColors: true }),
  };
}

function textSummary(data, options) {
  let summary = '\n';
  summary += '📊 Resumen de Prueba de Rendimiento\n';
  summary += '====================================\n\n';

  summary += `Duración total: ${data.state.testRunDurationMs / 1000}s\n`;
  summary += `VUs máximos: ${data.metrics.vus_max.values.max}\n`;
  summary += `Iteraciones: ${data.metrics.iterations.values.count}\n`;
  summary += `Requests totales: ${data.metrics.http_reqs.values.count}\n\n`;

  summary += '⏱️  Tiempos de Respuesta:\n';
  summary += `  - Media: ${data.metrics.http_req_duration.values.avg.toFixed(2)}ms\n`;
  summary += `  - Min: ${data.metrics.http_req_duration.values.min.toFixed(2)}ms\n`;
  summary += `  - Max: ${data.metrics.http_req_duration.values.max.toFixed(2)}ms\n`;
  summary += `  - p(95): ${data.metrics.http_req_duration.values['p(95)'].toFixed(2)}ms\n\n`;

  summary += '✅ Tasa de Éxito:\n';
  summary += `  - Requests exitosos: ${(100 - data.metrics.http_req_failed.values.rate * 100).toFixed(2)}%\n`;
  summary += `  - Tasa de error: ${(data.metrics.errors ? data.metrics.errors.values.rate * 100 : 0).toFixed(2)}%\n\n`;

  return summary;
}

