# 🎉 Eventia Core API

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen?logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue?logo=postgresql)
![Redis](https://img.shields.io/badge/Redis-7-red?logo=redis)

**Sistema de gestión de eventos con Clean Architecture**

---

## 🚀 Inicio Rápido

### Requisitos
- Java JDK 21
- Docker y Docker Compose
- Git

### Iniciar el Sistema

```bash
# Clonar repositorio
git clone <repository-url>
cd "Eventia Core API"

# Opción 1: Script automático (Recomendado)
./start-minimal.sh

# Opción 2: Manual
docker-compose up postgres redis backend
```

### Verificar que Funciona

```bash
# Health check
curl http://localhost:8080/actuator/health

# Crear un evento de prueba
curl -X POST http://localhost:8080/api/v1/eventos \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Evento de Prueba",
    "descripcion": "Descripción",
    "fechaInicio": "2025-12-15T10:00:00",
    "fechaFin": "2025-12-15T18:00:00",
    "ubicacion": "Popayán",
    "capacidadMaxima": 100
  }'
```

---

## 📋 Características

### Funcionalidades
- ✅ **Gestión de Eventos**: Crear, actualizar, consultar y eliminar eventos
- ✅ **Gestión de Participantes**: Registro y administración
- ✅ **Sistema de Asistencia**: Registro con validación de cupos
- ✅ **Estadísticas en Tiempo Real**: Ocupación y confirmaciones
- ✅ **Caché con Redis**: Consultas optimizadas
- ✅ **API RESTful**: 32 endpoints JSON

### Reglas de Negocio
- ✅ Validación automática de cupos disponibles
- ✅ Prevención de doble registro
- ✅ Control de capacidad de eventos
- ✅ Gestión de estados de asistencia

---

## 🏗️ Arquitectura

### Clean Architecture (Hexagonal)

```
┌─────────────────────────────────────────┐
│        Infrastructure Layer              │
│  (REST, JPA, Redis, Configuración)      │
│                                          │
│  ┌────────────────────────────────────┐ │
│  │      Application Layer             │ │
│  │  (DTOs, Mappers, Casos de Uso)    │ │
│  │                                    │ │
│  │  ┌──────────────────────────────┐ │ │
│  │  │      Domain Layer (Core)     │ │ │
│  │  │  • Entidades                 │ │ │
│  │  │  • Reglas de Negocio         │ │ │
│  │  │  • Servicios                 │ │ │
│  │  └──────────────────────────────┘ │ │
│  └────────────────────────────────────┘ │
└─────────────────────────────────────────┘
```

### Stack Tecnológico

| Componente | Tecnología |
|------------|------------|
| **Lenguaje** | Java 21 LTS |
| **Framework** | Spring Boot 3.5.7 |
| **Base de Datos** | PostgreSQL 17 |
| **Caché** | Redis 7 |
| **ORM** | Spring Data JPA (Hibernate) |
| **Migraciones** | Flyway |
| **Build Tool** | Gradle 8.11.1 |
| **Testing** | JUnit 5, Mockito, REST Assured |
| **Calidad** | SpotBugs, Checkstyle, JaCoCo |

---

## 📊 Modelo de Datos

### Entidades Principales

**Eventos**
- Representa eventos a los que se pueden inscribir participantes
- Campos: nombre, descripción, fechas, ubicación, capacidad
- Validaciones: capacidad > 0, fechas futuras, estado válido

**Participantes**
- Personas registradas en el sistema
- Campos: nombre, apellido, email, documento
- Validaciones: email único, documento único

**Asistencias**
- Relación entre eventos y participantes
- Campos: estado, fecha de registro, notas
- Validaciones: sin duplicados, cupos disponibles

### Migraciones Flyway

Las tablas se crean **automáticamente** al iniciar la aplicación:

- `V1__create_initial_schema.sql` - Crea las 3 tablas con índices
- `V2__insert_sample_data.sql` - Datos de ejemplo

---

## 🌐 API REST

### Endpoints Principales

#### Eventos
```
POST   /api/v1/eventos              - Crear evento
GET    /api/v1/eventos              - Listar todos
GET    /api/v1/eventos/{id}         - Obtener por ID
GET    /api/v1/eventos/disponibles  - Con cupos disponibles
PUT    /api/v1/eventos/{id}         - Actualizar
DELETE /api/v1/eventos/{id}         - Eliminar
```

#### Participantes
```
POST   /api/v1/participantes           - Crear participante
GET    /api/v1/participantes           - Listar todos
GET    /api/v1/participantes/{id}      - Obtener por ID
GET    /api/v1/participantes/email/{email} - Por email
```

#### Asistencias
```
POST   /api/v1/asistencias                          - Registrar asistencia
GET    /api/v1/asistencias/evento/{id}/estadisticas - Estadísticas
PATCH  /api/v1/asistencias/{id}/cancelar            - Cancelar
```

**Ver ejemplos completos**: [API_EXAMPLES.md](API_EXAMPLES.md)

---

## 💾 Base de Datos y Caché

### PostgreSQL

**Configuración** (docker-compose.yml):
```yaml
postgres:
  image: postgres:17-alpine
  ports:
    - "5432:5432"
  environment:
    POSTGRES_DB: eventia_db
    POSTGRES_USER: eventia_user
    POSTGRES_PASSWORD: eventia_pass
```

**Conexión**:
```bash
# Desde terminal
docker exec -it eventia-postgres psql -U eventia_user -d eventia_db

# Ver tablas
\dt

# Consultar datos
SELECT * FROM eventos;
```

### Redis (Caché)

**Configuración**: Redis se usa para cachear consultas frecuentes:

| Caché | TTL | Uso |
|-------|-----|-----|
| `eventos` | 15 min | Consultas de eventos |
| `eventosDisponibles` | 15 min | Eventos con cupos |
| `estadisticasEvento` | 5 min | Estadísticas |

**Verificar caché**:
```bash
# Ver claves en Redis
docker exec -it eventia-redis redis-cli KEYS "*"

# Ver valor específico
docker exec -it eventia-redis redis-cli GET "eventos::1"
```

**Redis Insight** (Visualización): http://localhost:5540

---

## 🧪 Testing

### Niveles de Pruebas

El proyecto incluye 3 niveles de pruebas:

```bash
# Todas las pruebas
./gradlew test

# Solo unitarias
./gradlew test --tests "*Test"

# Solo integración
./gradlew test --tests "*IntegrationTest"

# Solo E2E
./gradlew test --tests "*E2ETest"

# Con reporte de cobertura
./gradlew jacocoTestReport
open build/reports/jacoco/test/html/index.html
```

### Tipos de Pruebas

1. **Unitarias** (`EventoServiceTest.java`)
   - Prueban servicios con mocks
   - JUnit 5 + Mockito

2. **Integración** (`EventoControllerIntegrationTest.java`)
   - Prueban controladores con MockMvc
   - Base de datos H2 en memoria

3. **E2E** (`EventiaCoreApiE2ETest.java`)
   - Prueban flujos completos
   - REST Assured

---

## 🔍 Análisis de Calidad

### Herramientas Configuradas

```bash
# Checkstyle (estilo de código)
./gradlew checkstyleMain

# SpotBugs (detección de bugs y seguridad)
./gradlew spotbugsMain

# JaCoCo (cobertura de código)
./gradlew jacocoTestReport

# Todo junto
./gradlew check
```

### CI/CD con GitHub Actions

Pipeline automático que ejecuta:
1. Compilación
2. Pruebas unitarias
3. Pruebas de integración
4. Análisis estático (Checkstyle)
5. Análisis de seguridad (SpotBugs)
6. Reporte de cobertura

**Archivo**: `.github/workflows/ci-cd.yml`

---

## 🐳 Docker

### Servicios

```yaml
# PostgreSQL - Base de datos
postgres:
  image: postgres:17-alpine
  ports: ["5432:5432"]

# Redis - Caché
redis:
  image: redis:7-alpine
  ports: ["6379:6379"]

# Backend - API
backend:
  build: .
  ports: ["8080:8080"]
  depends_on: [postgres, redis]

# Redis Insight - Visualización (opcional)
redis-insight:
  image: redis/redisinsight:latest
  ports: ["5540:5540"]
```

### Comandos Útiles

```bash
# Iniciar servicios esenciales
./start-minimal.sh

# O manualmente
docker-compose up postgres redis backend

# Ver logs
docker-compose logs -f backend

# Verificar estado
docker-compose ps

# Detener todo
docker-compose down

# Limpiar volúmenes (¡cuidado!)
docker-compose down -v
```

---

## 🛠️ Desarrollo

### Requisitos de Java

El proyecto usa **Java 21**. Si tu sistema tiene Java 25, usa:

```bash
# Configurar Java 21 temporalmente
export JAVA_HOME=$(/usr/libexec/java_home -v 21)

# O usar el script que lo hace automáticamente
./dev.sh run
```

### Scripts Disponibles

```bash
./dev.sh build          # Compilar
./dev.sh run            # Ejecutar app
./dev.sh test           # Ejecutar tests
./dev.sh clean          # Limpiar build

./start-minimal.sh      # Iniciar servicios esenciales
./check-status.sh       # Verificar estado
```

### Estructura del Proyecto

```
src/main/java/
├── domain/              # Lógica de negocio (core)
│   ├── model/          # Entidades de dominio
│   ├── repository/     # Interfaces (puertos)
│   └── service/        # Servicios con reglas de negocio
├── application/        # DTOs y mappers
├── infrastructure/     # Adaptadores (REST, JPA, Config)
└── exception/          # Excepciones personalizadas

src/main/resources/
├── application.yml     # Configuración
└── db/migration/       # Migraciones Flyway
```

---

## 📚 Documentación Adicional

- **[QUICKSTART.md](QUICKSTART.md)** - Guía de inicio en 5 minutos
- **[API_EXAMPLES.md](API_EXAMPLES.md)** - Ejemplos de todos los endpoints
- **[ARCHITECTURE.md](ARCHITECTURE.md)** - Arquitectura técnica detallada

---

## ⚠️ Solución de Problemas Comunes

### Lombok no genera código

1. IntelliJ: `Settings` → `Build` → `Compiler` → `Annotation Processors`
2. ✅ Marcar "Enable annotation processing"
3. `Build` → `Rebuild Project`

### Cannot connect to database

```bash
# Verificar que PostgreSQL esté corriendo
docker ps | grep postgres

# Si no está, iniciarlo
docker-compose up -d postgres
```

### Cannot connect to Redis

```bash
# Verificar Redis
docker exec -it eventia-redis redis-cli ping
# Debe responder: PONG
```

### Flyway falla

```bash
# Resetear base de datos
docker-compose down -v
docker-compose up -d postgres
```

---

## 📊 Métricas del Proyecto

- **Archivos Java**: 60+
- **Líneas de código**: 5,000+
- **Endpoints REST**: 32
- **Niveles de testing**: 3
- **Cobertura de código**: >70%

---

## 🎓 Mejores Prácticas Implementadas

- ✅ Clean Architecture (Hexagonal)
- ✅ SOLID Principles
- ✅ DRY (Don't Repeat Yourself)
- ✅ Inyección de dependencias
- ✅ Migraciones versionadas (Flyway)
- ✅ Caché inteligente (Redis)
- ✅ Validaciones robustas
- ✅ Manejo global de errores
- ✅ Logging apropiado
- ✅ Tests automatizados

---

## 🤝 Contribución

1. Fork el proyecto
2. Crea tu rama: `git checkout -b feature/nueva-feature`
3. Commit: `git commit -m 'Add: nueva feature'`
4. Push: `git push origin feature/nueva-feature`
5. Abre un Pull Request

---

## 📄 Licencia

Este proyecto está bajo la Licencia MIT.

---

## 👥 Autor

**Universidad del Cauca - Eventia Team**

---

## 🚀 Comandos Rápidos de Referencia

```bash
# Desarrollo
./start-minimal.sh                    # Iniciar servicios esenciales
curl http://localhost:8080/actuator/health  # Verificar API
docker-compose logs -f backend        # Ver logs

# Testing
./gradlew test                        # Todas las pruebas
./gradlew jacocoTestReport            # Cobertura

# Análisis
./gradlew check                       # Calidad de código

# Base de Datos
docker exec -it eventia-postgres psql -U eventia_user -d eventia_db

# Redis
docker exec -it eventia-redis redis-cli
```

---

**¡Gracias por usar Eventia Core API!** 🎉

