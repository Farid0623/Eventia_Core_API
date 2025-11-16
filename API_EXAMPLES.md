# 📮 Ejemplos de API - Eventia Core API

Esta guía contiene ejemplos prácticos de uso de todos los endpoints de la API.

## 🌐 Base URL

```
http://localhost:8080/api/v1
```

---

## 📅 EVENTOS

### 1. Crear un Evento

```bash
curl -X POST http://localhost:8080/api/v1/eventos \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Conferencia de Tecnología 2025",
    "descripcion": "Evento anual sobre las últimas tendencias en tecnología",
    "fechaInicio": "2025-12-01T09:00:00",
    "fechaFin": "2025-12-01T18:00:00",
    "ubicacion": "Centro de Convenciones Popayán",
    "capacidadMaxima": 200
  }'
```

### 2. Obtener Todos los Eventos

```bash
curl http://localhost:8080/api/v1/eventos
```

### 3. Obtener un Evento por ID

```bash
curl http://localhost:8080/api/v1/eventos/1
```

### 4. Obtener Eventos Disponibles (con cupos)

```bash
curl http://localhost:8080/api/v1/eventos/disponibles
```

### 5. Obtener Eventos Próximos

```bash
curl http://localhost:8080/api/v1/eventos/proximos
```

### 6. Obtener Eventos por Estado

```bash
curl http://localhost:8080/api/v1/eventos/estado/ACTIVO
```

Estados disponibles: `ACTIVO`, `CANCELADO`, `FINALIZADO`, `BORRADOR`

### 7. Actualizar un Evento

```bash
curl -X PUT http://localhost:8080/api/v1/eventos/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Conferencia Tech 2025 - ACTUALIZADO",
    "descripcion": "Evento actualizado",
    "fechaInicio": "2025-12-01T09:00:00",
    "fechaFin": "2025-12-01T19:00:00",
    "ubicacion": "Centro de Convenciones Popayán",
    "capacidadMaxima": 250
  }'
```

### 8. Cambiar Estado de un Evento

```bash
curl -X PATCH http://localhost:8080/api/v1/eventos/1/estado/FINALIZADO
```

### 9. Verificar Capacidad Disponible

```bash
curl http://localhost:8080/api/v1/eventos/1/capacidad-disponible
```

### 10. Eliminar un Evento

```bash
curl -X DELETE http://localhost:8080/api/v1/eventos/1
```

---

## 👥 PARTICIPANTES

### 1. Crear un Participante

```bash
curl -X POST http://localhost:8080/api/v1/participantes \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan",
    "apellido": "Pérez",
    "email": "juan.perez@example.com",
    "telefono": "3001234567",
    "documento": "1234567890",
    "tipoDocumento": "CEDULA_CIUDADANIA"
  }'
```

Tipos de documento: `CEDULA_CIUDADANIA`, `CEDULA_EXTRANJERIA`, `PASAPORTE`, `TARJETA_IDENTIDAD`

### 2. Obtener Todos los Participantes

```bash
curl http://localhost:8080/api/v1/participantes
```

### 3. Obtener un Participante por ID

```bash
curl http://localhost:8080/api/v1/participantes/1
```

### 4. Buscar Participante por Email

```bash
curl http://localhost:8080/api/v1/participantes/email/juan.perez@example.com
```

### 5. Buscar Participante por Documento

```bash
curl http://localhost:8080/api/v1/participantes/documento/1234567890
```

### 6. Actualizar un Participante

```bash
curl -X PUT http://localhost:8080/api/v1/participantes/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan Carlos",
    "apellido": "Pérez González",
    "email": "juan.perez@example.com",
    "telefono": "3009876543",
    "documento": "1234567890",
    "tipoDocumento": "CEDULA_CIUDADANIA"
  }'
```

### 7. Eliminar un Participante

```bash
curl -X DELETE http://localhost:8080/api/v1/participantes/1
```

---

## ✅ ASISTENCIAS

### 1. Registrar Asistencia (Inscribir participante a evento)

```bash
curl -X POST http://localhost:8080/api/v1/asistencias \
  -H "Content-Type: application/json" \
  -d '{
    "eventoId": 1,
    "participanteId": 1,
    "notas": "Registro confirmado por el participante"
  }'
```

### 2. Obtener una Asistencia por ID

```bash
curl http://localhost:8080/api/v1/asistencias/1
```

### 3. Obtener Todas las Asistencias de un Evento

```bash
curl http://localhost:8080/api/v1/asistencias/evento/1
```

### 4. Obtener Todas las Asistencias de un Participante

```bash
curl http://localhost:8080/api/v1/asistencias/participante/1
```

### 5. Obtener Estadísticas de un Evento

```bash
curl http://localhost:8080/api/v1/asistencias/evento/1/estadisticas
```

Respuesta ejemplo:
```json
{
  "eventoId": 1,
  "nombreEvento": "Conferencia Tech 2025",
  "capacidadMaxima": 200,
  "totalRegistrados": 50,
  "confirmados": 45,
  "cancelados": 5,
  "asistieron": 40,
  "noAsistieron": 5,
  "cuposDisponibles": 150,
  "porcentajeOcupacion": 25.0
}
```

### 6. Cancelar una Asistencia

```bash
curl -X PATCH http://localhost:8080/api/v1/asistencias/1/cancelar
```

### 7. Marcar que Asistió

```bash
curl -X PATCH http://localhost:8080/api/v1/asistencias/1/marcar-asistio
```

### 8. Marcar que NO Asistió

```bash
curl -X PATCH http://localhost:8080/api/v1/asistencias/1/marcar-no-asistio
```

### 9. Eliminar una Asistencia

```bash
curl -X DELETE http://localhost:8080/api/v1/asistencias/1
```

---

## 🔍 ACTUATOR (Monitoreo)

### Health Check

```bash
curl http://localhost:8080/actuator/health
```

### Información de la Aplicación

```bash
curl http://localhost:8080/actuator/info
```

### Métricas

```bash
curl http://localhost:8080/actuator/metrics
```

### Métricas Específicas

```bash
curl http://localhost:8080/actuator/metrics/jvm.memory.used
```

---

## 📝 Flujo de Ejemplo Completo

### Escenario: Registrar participante en un evento

```bash
# 1. Crear un evento
curl -X POST http://localhost:8080/api/v1/eventos \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Workshop de Spring Boot",
    "descripcion": "Taller práctico",
    "fechaInicio": "2025-11-20T14:00:00",
    "fechaFin": "2025-11-20T18:00:00",
    "ubicacion": "Universidad del Cauca",
    "capacidadMaxima": 50
  }'

# Respuesta: { "id": 1, ... }

# 2. Crear un participante
curl -X POST http://localhost:8080/api/v1/participantes \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "María",
    "apellido": "González",
    "email": "maria.gonzalez@example.com",
    "telefono": "3009876543",
    "documento": "0987654321",
    "tipoDocumento": "CEDULA_CIUDADANIA"
  }'

# Respuesta: { "id": 1, ... }

# 3. Registrar asistencia
curl -X POST http://localhost:8080/api/v1/asistencias \
  -H "Content-Type: application/json" \
  -d '{
    "eventoId": 1,
    "participanteId": 1,
    "notas": "Confirmado por correo"
  }'

# Respuesta: { "id": 1, "estado": "CONFIRMADO", ... }

# 4. Verificar estadísticas
curl http://localhost:8080/api/v1/asistencias/evento/1/estadisticas

# 5. Obtener evento actualizado
curl http://localhost:8080/api/v1/eventos/1
# Verás participantesRegistrados = 1
```

---

## ⚠️ Manejo de Errores

### Ejemplo: Doble Registro (Regla de Negocio)

```bash
# Intentar registrar el mismo participante dos veces
curl -X POST http://localhost:8080/api/v1/asistencias \
  -H "Content-Type: application/json" \
  -d '{
    "eventoId": 1,
    "participanteId": 1,
    "notas": "Intento de duplicado"
  }'
```

Respuesta (422 Unprocessable Entity):
```json
{
  "timestamp": "2025-11-14T22:00:00",
  "status": 422,
  "error": "Business Rule Violation",
  "message": "El participante ya está registrado en este evento",
  "path": "/api/v1/asistencias"
}
```

### Ejemplo: Capacidad Llena

```bash
# Intentar registrar cuando el evento está lleno
curl -X POST http://localhost:8080/api/v1/asistencias \
  -H "Content-Type: application/json" \
  -d '{
    "eventoId": 1,
    "participanteId": 2,
    "notas": "Registro"
  }'
```

Respuesta (422):
```json
{
  "timestamp": "2025-11-14T22:00:00",
  "status": 422,
  "error": "Business Rule Violation",
  "message": "El evento ha alcanzado su capacidad máxima",
  "path": "/api/v1/asistencias"
}
```

### Ejemplo: Validación de Datos

```bash
# Email inválido
curl -X POST http://localhost:8080/api/v1/participantes \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Test",
    "apellido": "User",
    "email": "email-invalido",
    "documento": "123",
    "tipoDocumento": "CEDULA_CIUDADANIA"
  }'
```

Respuesta (400 Bad Request):
```json
{
  "timestamp": "2025-11-14T22:00:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Error en la validación de los datos",
  "path": "/api/v1/participantes",
  "validationErrors": {
    "email": "El email debe ser válido",
    "documento": "El documento debe tener entre 5 y 50 caracteres"
  }
}
```

### Ejemplo: Recurso No Encontrado

```bash
curl http://localhost:8080/api/v1/eventos/9999
```

Respuesta (404 Not Found):
```json
{
  "timestamp": "2025-11-14T22:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Evento no encontrado con id: '9999'",
  "path": "/api/v1/eventos/9999"
}
```

---

## 🧪 Testing con HTTPie (Alternativa a curl)

Si prefieres una herramienta más amigable, instala HTTPie:

```bash
brew install httpie
```

Ejemplos:

```bash
# GET
http GET localhost:8080/api/v1/eventos

# POST
http POST localhost:8080/api/v1/eventos \
  nombre="Evento Test" \
  descripcion="Descripción" \
  fechaInicio="2025-12-01T10:00:00" \
  fechaFin="2025-12-01T18:00:00" \
  ubicacion="Popayán" \
  capacidadMaxima:=100

# PATCH
http PATCH localhost:8080/api/v1/asistencias/1/cancelar

# DELETE
http DELETE localhost:8080/api/v1/eventos/1
```

---

## 📊 Colección de Postman

Puedes importar esta colección en Postman para probar todos los endpoints:

**URL de colección**: (Agregar cuando esté disponible)

O crear tu propia colección con estos endpoints.

---

## 🔄 Códigos de Estado HTTP

| Código | Significado | Cuándo se usa |
|--------|-------------|---------------|
| 200 | OK | Operación exitosa (GET, PUT, PATCH) |
| 201 | Created | Recurso creado (POST) |
| 204 | No Content | Eliminación exitosa (DELETE) |
| 400 | Bad Request | Datos de entrada inválidos |
| 404 | Not Found | Recurso no existe |
| 409 | Conflict | Recurso duplicado (email, documento) |
| 422 | Unprocessable Entity | Violación de regla de negocio |
| 500 | Internal Server Error | Error del servidor |

---

**¡Feliz Testing!** 🚀

Para más información, consulta el [README.md](README.md) o [QUICKSTART.md](QUICKSTART.md).

