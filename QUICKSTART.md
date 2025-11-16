# 🚀 Guía de Inicio Rápido - Eventia Core API

Esta guía te ayudará a poner en marcha el proyecto en menos de 5 minutos.

## ⚡ Inicio Rápido (Con Docker)

### Opción 1: Todo con Docker Compose (Recomendado)

```bash
# 1. Levantar todos los servicios
docker-compose up -d

# 2. Verificar que todo esté funcionando
curl http://localhost:8080/actuator/health
```

¡Listo! La API estará disponible en `http://localhost:8080`

### Servicios Disponibles

- **API Backend**: http://localhost:8080
- **PostgreSQL**: localhost:5432
- **Redis**: localhost:6379
- **Redis Insight**: http://localhost:5540

---

## 🛠️ Desarrollo Local (Sin Docker)

### Pre-requisitos

Asegúrate de tener instalado:
- Java JDK 21 (requerido)
- Docker (para PostgreSQL y Redis)

### Pasos

#### 1. Iniciar PostgreSQL y Redis

```bash
# PostgreSQL
docker run -d --name eventia-postgres \
  -e POSTGRES_DB=eventia_db \
  -e POSTGRES_USER=eventia_user \
  -e POSTGRES_PASSWORD=eventia_pass \
  -p 5432:5432 \
  postgres:17-alpine

# Redis
docker run -d --name eventia-redis \
  -p 6379:6379 \
  redis:7-alpine
```

#### 2. Compilar y Ejecutar

```bash
# Usando el script de desarrollo (recomendado)
./dev.sh run

# O manualmente con Java 21
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
./gradlew bootRun
```

La API estará disponible en: `http://localhost:8080`

---

## 📝 Script de Desarrollo

Hemos incluido un script `dev.sh` que facilita tareas comunes:

```bash
# Ver todos los comandos disponibles
./dev.sh

# Ejemplos de uso
./dev.sh build              # Compilar proyecto
./dev.sh test               # Ejecutar todas las pruebas
./dev.sh test-unit          # Solo pruebas unitarias
./dev.sh test-integration   # Solo pruebas de integración
./dev.sh run                # Iniciar la aplicación
./dev.sh coverage           # Reporte de cobertura
./dev.sh docker-up          # Levantar servicios Docker
./dev.sh docker-down        # Detener servicios Docker
```

**Nota importante**: El script automáticamente usa Java 21, así que no necesitas configurar JAVA_HOME manualmente.

---

## 🧪 Probar la API

### 1. Verificar que la API esté funcionando

```bash
curl http://localhost:8080/actuator/health
```

Respuesta esperada:
```json
{
  "status": "UP"
}
```

### 2. Crear un Evento

```bash
curl -X POST http://localhost:8080/api/v1/eventos \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Mi Primer Evento",
    "descripcion": "Evento de prueba",
    "fechaInicio": "2025-12-15T10:00:00",
    "fechaFin": "2025-12-15T18:00:00",
    "ubicacion": "Popayán",
    "capacidadMaxima": 100
  }'
```

### 3. Obtener Todos los Eventos

```bash
curl http://localhost:8080/api/v1/eventos
```

### 4. Crear un Participante

```bash
curl -X POST http://localhost:8080/api/v1/participantes \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan",
    "apellido": "Pérez",
    "email": "juan@example.com",
    "telefono": "3001234567",
    "documento": "1234567890",
    "tipoDocumento": "CEDULA_CIUDADANIA"
  }'
```

### 5. Registrar Asistencia

```bash
curl -X POST http://localhost:8080/api/v1/asistencias \
  -H "Content-Type: application/json" \
  -d '{
    "eventoId": 1,
    "participanteId": 1,
    "notas": "Registro de prueba"
  }'
```

### 6. Obtener Estadísticas

```bash
curl http://localhost:8080/api/v1/asistencias/evento/1/estadisticas
```

---

## 🐳 Comandos Docker Útiles

```bash
# Ver logs de la aplicación
docker-compose logs -f backend

# Ver logs de PostgreSQL
docker-compose logs -f postgres

# Ver logs de Redis
docker-compose logs -f redis

# Reiniciar solo el backend
docker-compose restart backend

# Detener todo y eliminar volúmenes
docker-compose down -v

# Ver estado de los contenedores
docker-compose ps
```

---

## 🔍 Acceder a Redis Insight

1. Abre tu navegador en: http://localhost:5540
2. Haz clic en "Add Redis Database"
3. Configura:
   - **Host**: redis (si estás usando docker-compose) o localhost
   - **Port**: 6379
   - **Database Alias**: Eventia Cache

Ahora podrás ver todos los datos en caché en tiempo real.

---

## 🧪 Ejecutar Pruebas

```bash
# Todas las pruebas
./dev.sh test

# Solo unitarias
./dev.sh test-unit

# Solo integración
./dev.sh test-integration

# Solo E2E
./dev.sh test-e2e

# Ver reporte de cobertura
./dev.sh coverage
open build/reports/jacoco/test/html/index.html
```

---

## 🔒 Análisis de Seguridad

```bash
# Checkstyle (estilo de código)
./dev.sh checkstyle

# SpotBugs (detección de bugs y vulnerabilidades)
./dev.sh spotbugs

# Ejecutar todo el análisis
./dev.sh check
```

---

## 📊 Endpoints del Actuator

El proyecto incluye Spring Actuator para monitoreo:

- **Health**: http://localhost:8080/actuator/health
- **Info**: http://localhost:8080/actuator/info
- **Metrics**: http://localhost:8080/actuator/metrics

---

## ❓ Solución de Problemas

### Problema: "Unsupported class file major version 69"

**Solución**: Asegúrate de usar Java 21. El script `dev.sh` lo hace automáticamente, o ejecuta:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
```

### Problema: "Port 8080 already in use"

**Solución**: Detén cualquier servicio en el puerto 8080 o cambia el puerto en `application.yml`:

```yaml
server:
  port: 8081
```

### Problema: "Connection refused to PostgreSQL"

**Solución**: Verifica que PostgreSQL esté corriendo:

```bash
docker ps | grep postgres

# Si no está corriendo, inícialo
docker start eventia-postgres
```

### Problema: "Connection refused to Redis"

**Solución**: Verifica que Redis esté corriendo:

```bash
docker ps | grep redis

# Si no está corriendo, inícialo
docker start eventia-redis
```

---

## 📚 Más Información

- Ver el [README.md](README.md) completo para documentación detallada
- Estructura del proyecto y arquitectura
- Endpoints completos de la API
- Guía de contribución

---

## ✨ Próximos Pasos

1. ✅ Explorar los endpoints de la API
2. ✅ Probar el sistema de caché con Redis Insight
3. ✅ Revisar las pruebas automatizadas
4. ✅ Personalizar la configuración en `application.yml`
5. ✅ Contribuir al proyecto

---

**¡Feliz Desarrollo!** 🎉

Si tienes problemas, consulta el README completo o abre un issue en GitHub.

