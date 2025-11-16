# 🏗️ Documentación de Arquitectura - Eventia Core API

## 📋 Tabla de Contenidos

1. [Visión General](#visión-general)
2. [Arquitectura del Sistema](#arquitectura-del-sistema)
3. [Stack Tecnológico](#stack-tecnológico)
4. [Modelo de Datos](#modelo-de-datos)
5. [Flujo de Datos](#flujo-de-datos)
6. [Sistema de Caché](#sistema-de-caché)
7. [Configuración de Base de Datos](#configuración-de-base-de-datos)
8. [Estructura de Carpetas](#estructura-de-carpetas)
9. [Patrones de Diseño](#patrones-de-diseño)
10. [Guías de Desarrollo](#guías-de-desarrollo)

---

## 1. Visión General

**Eventia Core API** es un sistema backend para gestión de eventos construido siguiendo los principios de **Clean Architecture (Arquitectura Hexagonal)**. El sistema permite:

- ✅ Crear y gestionar eventos
- ✅ Registrar participantes
- ✅ Gestionar asistencias con validación de reglas de negocio
- ✅ Consultar estadísticas en tiempo real
- ✅ Caché distribuido con Redis

### Principios de Diseño

- **Separación de Responsabilidades**: Cada capa tiene una responsabilidad única
- **Independencia de Frameworks**: El dominio no depende de Spring
- **Testabilidad**: Componentes desacoplados facilitan las pruebas
- **Inversión de Dependencias**: Las capas externas dependen de las internas

---

## 2. Arquitectura del Sistema

### 2.1 Clean Architecture - Capas

```
┌─────────────────────────────────────────────────────────┐
│                   INFRASTRUCTURE                         │
│  (Adaptadores: REST Controllers, JPA, Redis Config)    │
│                                                          │
│  ┌───────────────────────────────────────────────────┐ │
│  │              APPLICATION LAYER                     │ │
│  │    (DTOs, Mappers, Casos de Uso)                  │ │
│  │                                                    │ │
│  │  ┌──────────────────────────────────────────────┐ │ │
│  │  │          DOMAIN LAYER (CORE)                 │ │ │
│  │  │   • Entidades de Dominio                     │ │ │
│  │  │   • Reglas de Negocio                        │ │ │
│  │  │   • Interfaces (Puertos)                     │ │ │
│  │  │   • Servicios de Dominio                     │ │ │
│  │  └──────────────────────────────────────────────┘ │ │
│  └───────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
```

### 2.2 Descripción de Capas

#### 🔷 Capa de Dominio (Core)
**Ubicación**: `src/main/java/cue/edu/co/eventia_core_api/domain/`

**Responsabilidad**: Contiene la lógica de negocio pura, independiente de frameworks.

**Componentes**:
- **model/**: Entidades de dominio (Evento, Participante, Asistencia)
- **repository/**: Interfaces (puertos) que definen contratos
- **service/**: Servicios con lógica de negocio

**Características**:
- ✅ Sin dependencias externas (solo Java puro)
- ✅ Reglas de negocio centralizadas
- ✅ Altamente testeable

**Ejemplo**:
```java
// Entidad de dominio - Solo lógica de negocio
public class Evento {
    private Long id;
    private String nombre;
    private Integer capacidadMaxima;
    private Integer participantesRegistrados;
    
    // Regla de negocio
    public boolean tieneCapacidadDisponible() {
        return participantesRegistrados < capacidadMaxima;
    }
}
```

#### 🔶 Capa de Aplicación
**Ubicación**: `src/main/java/cue/edu/co/eventia_core_api/application/`

**Responsabilidad**: Define casos de uso y coordina el flujo entre capas.

**Componentes**:
- **dto/**: Data Transfer Objects (entrada/salida de API)
- **mapper/**: Conversión entre Domain y DTOs (MapStruct)

**Características**:
- ✅ Orquesta la lógica de dominio
- ✅ Define contratos de API
- ✅ Validaciones de entrada

**Ejemplo**:
```java
// DTO de entrada - Validaciones de API
public class CrearEventoRequest {
    @NotBlank
    @Size(min = 3, max = 200)
    private String nombre;
    
    @NotNull
    @Min(1)
    private Integer capacidadMaxima;
}
```

#### 🔵 Capa de Infraestructura
**Ubicación**: `src/main/java/cue/edu/co/eventia_core_api/infrastructure/`

**Responsabilidad**: Implementa los adaptadores hacia sistemas externos.

**Componentes**:
- **rest/**: Controladores REST (Spring MVC)
- **persistence/**: Implementación JPA/Hibernate
  - **entity/**: Entidades JPA (anotaciones @Entity)
  - **repository/**: Repositorios Spring Data JPA
  - **adapter/**: Implementación de puertos de dominio
- **config/**: Configuraciones (Redis, Spring, etc.)

**Características**:
- ✅ Implementa interfaces del dominio
- ✅ Maneja frameworks externos (Spring, JPA, Redis)
- ✅ Transacciones y persistencia

---

## 3. Stack Tecnológico

### 3.1 Backend Core

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **Java** | 21 LTS | Lenguaje principal |
| **Spring Boot** | 3.5.7 | Framework de aplicación |
| **Spring Data JPA** | Incluido | ORM y persistencia |
| **Hibernate** | Incluido | Implementación JPA |
| **Gradle** | 8.11.1 | Build tool |

**¿Por qué Java 21?**
- ✅ LTS (Long Term Support) hasta 2029
- ✅ Records, Pattern Matching, Virtual Threads
- ✅ Rendimiento mejorado
- ✅ Amplio soporte de librerías

**¿Por qué Spring Boot?**
- ✅ Ecosistema maduro y extenso
- ✅ Autoconfiguración inteligente
- ✅ Producción-ready (Actuator, Metrics)
- ✅ Gran comunidad y documentación

### 3.2 Base de Datos

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **PostgreSQL** | 17 | Base de datos principal |
| **Flyway** | Latest | Migraciones de BD |
| **H2** | Latest | BD en memoria para tests |

**¿Por qué PostgreSQL?**
- ✅ ACID compliant (transacciones seguras)
- ✅ Soporte JSON y tipos avanzados
- ✅ Rendimiento excelente
- ✅ Open source y gratuito

**Flyway**:
- ✅ Control de versiones de BD
- ✅ Migraciones automáticas
- ✅ Rollback seguro

### 3.3 Sistema de Caché

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **Redis** | 7 | Caché distribuido |
| **Spring Cache** | Incluido | Abstracción de caché |
| **Jedis** | 5.1.0 | Cliente Redis |

**¿Por qué Redis?**
- ✅ Extremadamente rápido (operaciones en memoria)
- ✅ Estructuras de datos versátiles
- ✅ TTL automático
- ✅ Persistencia opcional

### 3.4 Herramientas de Desarrollo

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **Lombok** | Latest | Reduce boilerplate |
| **MapStruct** | 1.6.3 | Mapeo de objetos |
| **Spring DevTools** | Incluido | Hot reload |

**Lombok**:
- ✅ Genera getters/setters automáticamente
- ✅ @Builder para constructores fluidos
- ✅ @Slf4j para logging

**MapStruct**:
- ✅ Mapeo type-safe en compile-time
- ✅ Alto rendimiento (código generado)
- ✅ Sin reflection

### 3.5 Testing

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **JUnit 5** | Latest | Framework de pruebas |
| **Mockito** | Latest | Mocking |
| **AssertJ** | Latest | Assertions fluidas |
| **REST Assured** | 5.5.0 | Tests E2E de API |
| **Spring Test** | Incluido | Tests de integración |

### 3.6 Calidad de Código

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **SpotBugs** | 4.8.6 | Detección de bugs |
| **FindSecBugs** | 1.13.0 | Análisis de seguridad |
| **Checkstyle** | 10.20.2 | Estilo de código |
| **JaCoCo** | 0.8.12 | Cobertura de código |

---

## 4. Modelo de Datos

### 4.1 Diagrama Entidad-Relación

```
┌─────────────────┐         ┌──────────────────┐         ┌─────────────────┐
│     EVENTOS     │         │   ASISTENCIAS    │         │ PARTICIPANTES   │
├─────────────────┤         ├──────────────────┤         ├─────────────────┤
│ id (PK)         │◄───────│ evento_id (FK)   │         │ id (PK)         │
│ nombre          │         │ participante_id  │────────►│ nombre          │
│ descripcion     │         │   (FK)           │         │ apellido        │
│ fecha_inicio    │         │ fecha_registro   │         │ email (UK)      │
│ fecha_fin       │         │ estado           │         │ telefono        │
│ ubicacion       │         │ notas            │         │ documento (UK)  │
│ capacidad_max   │         │ fecha_actualizac.│         │ tipo_documento  │
│ participantes   │         └──────────────────┘         │ fecha_creacion  │
│   _registrados  │                                      │ fecha_actualiz. │
│ estado          │                                      └─────────────────┘
│ fecha_creacion  │
│ fecha_actualiz. │
└─────────────────┘
```

### 4.2 Descripción de Entidades

#### 📅 Eventos
**Propósito**: Representa un evento al que los participantes pueden registrarse.

**Campos Principales**:
- `id`: Identificador único (generado automáticamente)
- `nombre`: Nombre del evento (requerido, 3-200 caracteres)
- `capacidad_maxima`: Número máximo de participantes (requerido, > 0)
- `participantes_registrados`: Contador automático (gestionado por el sistema)
- `estado`: ACTIVO, CANCELADO, FINALIZADO, BORRADOR

**Reglas de Negocio**:
- ✅ La fecha de inicio no puede ser en el pasado
- ✅ La fecha de fin debe ser posterior a la fecha de inicio
- ✅ La capacidad debe ser mayor a 0
- ✅ Los participantes registrados no pueden exceder la capacidad

**Índices**:
- `idx_evento_estado` - Para consultas por estado
- `idx_evento_fecha_inicio` - Para consultas de eventos próximos

#### 👥 Participantes
**Propósito**: Representa una persona que puede registrarse en eventos.

**Campos Principales**:
- `id`: Identificador único
- `email`: Email único (validado, requerido)
- `documento`: Documento único (validado, requerido)
- `tipo_documento`: CEDULA_CIUDADANIA, CEDULA_EXTRANJERIA, PASAPORTE, TARJETA_IDENTIDAD

**Reglas de Negocio**:
- ✅ El email debe ser único en el sistema
- ✅ El documento debe ser único en el sistema
- ✅ El email debe ser válido (formato)
- ✅ El teléfono debe tener 10 dígitos

**Índices**:
- `idx_participante_email` - Para búsquedas por email
- `idx_participante_documento` - Para búsquedas por documento

#### ✅ Asistencias (Tabla de Relación)
**Propósito**: Relaciona participantes con eventos, gestiona el registro.

**Campos Principales**:
- `id`: Identificador único
- `evento_id`: Referencia al evento (FK)
- `participante_id`: Referencia al participante (FK)
- `estado`: CONFIRMADO, CANCELADO, ASISTIO, NO_ASISTIO, EN_ESPERA

**Reglas de Negocio**:
- ✅ Un participante solo puede registrarse una vez por evento (UK)
- ✅ No se puede registrar si el evento está lleno
- ✅ No se puede registrar si el evento no está ACTIVO
- ✅ Al registrarse, incrementa el contador del evento
- ✅ Al cancelarse, decrementa el contador del evento

**Índices**:
- `idx_asistencia_evento` - Para consultas por evento
- `idx_asistencia_participante` - Para consultas por participante
- `idx_asistencia_estado` - Para filtrar por estado

### 4.3 Migraciones de Base de Datos

**Ubicación**: `src/main/resources/db/migration/`

#### V1__create_initial_schema.sql
Crea las tablas principales con:
- ✅ Definiciones de columnas
- ✅ Constraints (PK, FK, UK, CHECK)
- ✅ Índices optimizados
- ✅ Comentarios de documentación

#### V2__insert_sample_data.sql
Inserta datos de ejemplo:
- ✅ 3 eventos de muestra
- ✅ 5 participantes de ejemplo

**Flyway se ejecuta automáticamente** al iniciar la aplicación.

---

## 5. Flujo de Datos

### 5.1 Flujo de Creación de Evento

```
[Cliente HTTP]
    │
    │ POST /api/v1/eventos
    │ {nombre, fecha, capacidad, ...}
    ▼
[EventoController]
    │ Recibe CrearEventoRequest
    │ Validaciones automáticas (@Valid)
    ▼
[EventoMapper]
    │ Convierte DTO → Domain
    ▼
[EventoService]
    │ Aplica reglas de negocio:
    │ • Valida fechas
    │ • Valida capacidad
    │ • Establece estado inicial
    ▼
[EventoRepository (Puerto)]
    │
    ▼
[EventoRepositoryAdapter]
    │
    ▼
[EventoEntityMapper]
    │ Convierte Domain → JPA Entity
    ▼
[JpaEventoRepository]
    │
    ▼
[PostgreSQL]
    │ INSERT INTO eventos ...
    │
    ◄──── Respuesta ────
    │
[EventoController]
    │ 201 Created
    │ EventoResponse (DTO)
    ▼
[Cliente HTTP]
```

### 5.2 Flujo de Registro de Asistencia (Con Validaciones)

```
[Cliente]
    │ POST /api/v1/asistencias
    ▼
[AsistenciaController]
    │
    ▼
[AsistenciaService]
    │
    ├─► [EventoService.obtenerEventoPorId]
    │   ├─► ¿Existe el evento?
    │   ├─► ¿Está ACTIVO?
    │   └─► ¿Ha finalizado?
    │
    ├─► [ParticipanteService.obtenerParticipantePorId]
    │   └─► ¿Existe el participante?
    │
    ├─► [AsistenciaRepository.existsByEventoAndParticipante]
    │   └─► ¿Ya está registrado? → BusinessRuleException
    │
    ├─► [Evento.tieneCapacidadDisponible]
    │   └─► ¿Hay cupos? → BusinessRuleException si está lleno
    │
    ├─► [AsistenciaRepository.save]
    │   └─► Guarda asistencia con estado CONFIRMADO
    │
    └─► [EventoService.incrementarParticipantes]
        └─► Incrementa contador + invalida caché
    │
    ▼
[Cliente] ← 201 Created + AsistenciaResponse
```

### 5.3 Flujo con Caché (Redis)

```
[Cliente]
    │ GET /api/v1/eventos/1
    ▼
[EventoController]
    │
    ▼
[EventoService.obtenerEventoPorId]
    │
    ├─► Spring Cache (@Cacheable)
    │   │
    │   ├─► ¿Está en Redis? (key: eventos::1)
    │   │   │
    │   │   ├─► SÍ → Retorna desde Redis (fast!)
    │   │   │
    │   │   └─► NO → Continúa al repositorio
    │   │
    │   ▼
[EventoRepository.findById]
    │
    ▼
[PostgreSQL]
    │ SELECT * FROM eventos WHERE id = 1
    │
    ◄──── Evento ────
    │
[Spring Cache]
    │ Guarda en Redis con TTL
    │
    ▼
[Cliente] ← EventoResponse
```

**Invalidación de Caché**:
```
[Actualización/Eliminación]
    │
    ▼
[@CacheEvict(value = "eventos", allEntries = true)]
    │ Limpia TODO el caché de eventos
    │
    ▼
[Próximas consultas cargarán datos frescos]
```

---

## 6. Sistema de Caché

### 6.1 Configuración de Redis

**Archivo**: `infrastructure/config/RedisConfig.java`

```java
@Configuration
@EnableCaching
public class RedisConfig {
    // Configuración de serializadores
    // TTL por caché
    // Transacciones
}
```

### 6.2 Cachés Configurados

| Nombre | TTL | Uso | Invalidación |
|--------|-----|-----|--------------|
| `eventos` | 15 min | Consultas de eventos individuales | Al crear/actualizar/eliminar evento |
| `eventosDisponibles` | 15 min | Lista de eventos con cupos | Al cambiar capacidad/asistencias |
| `eventosProximos` | 15 min | Lista de próximos eventos | Al crear/actualizar evento |
| `participantes` | 10 min | Consultas de participantes | Al crear/actualizar participante |
| `asistencias` | 10 min | Consultas de asistencias | Al registrar/cancelar asistencia |
| `estadisticasEvento` | 5 min | Estadísticas calculadas | Al cambiar asistencias |
| `capacidadEvento` | 2 min | Verificación rápida de cupos | Al registrar/cancelar asistencia |

### 6.3 Estrategia de Caché

**Cache-Aside Pattern**:
1. Aplicación consulta caché
2. Si existe (HIT) → retorna
3. Si no existe (MISS) → consulta BD
4. Guarda en caché para próximas consultas

**Write-Through Pattern** (para actualizaciones):
1. Actualiza en BD
2. Invalida caché
3. Próxima lectura recarga datos frescos

### 6.4 Conexión de Redis con Backend

#### Paso 1: Dependencias (build.gradle)
```gradle
implementation 'org.springframework.boot:spring-boot-starter-data-redis'
implementation 'org.springframework.boot:spring-boot-starter-cache'
implementation 'redis.clients:jedis:5.1.0'
```

#### Paso 2: Configuración (application.yml)
```yaml
spring:
  data:
    redis:
      host: localhost  # redis (en Docker)
      port: 6379
      timeout: 60000
  
  cache:
    type: redis
    redis:
      time-to-live: 600000  # 10 minutos por defecto
```

#### Paso 3: Habilitar en Código
```java
@Configuration
@EnableCaching  // ← Habilita el sistema de caché
public class RedisConfig {
    // Configuración del CacheManager
}
```

#### Paso 4: Usar en Servicios
```java
@Service
public class EventoService {
    
    @Cacheable(value = "eventos", key = "#id")
    public Evento obtenerEventoPorId(Long id) {
        // Se cachea automáticamente
    }
    
    @CacheEvict(value = "eventos", allEntries = true)
    public Evento actualizarEvento(Long id, Evento evento) {
        // Limpia el caché automáticamente
    }
}
```

---

## 7. Configuración de Base de Datos

### 7.1 PostgreSQL en Docker

**docker-compose.yml**:
```yaml
postgres:
  image: postgres:17-alpine
  container_name: eventia-postgres
  environment:
    POSTGRES_DB: eventia_db
    POSTGRES_USER: eventia_user
    POSTGRES_PASSWORD: eventia_pass
  ports:
    - "5432:5432"
  volumes:
    - postgres_data:/var/lib/postgresql/data
```

### 7.2 Configuración de Conexión

**application.yml**:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/eventia_db
    username: eventia_user
    password: eventia_pass
    driver-class-name: org.postgresql.Driver
    hikari:
      connection-timeout: 20000
      maximum-pool-size: 10
      minimum-idle: 5
```

### 7.3 Configuración JPA

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate  # No crear tablas, solo validar
    show-sql: true  # Mostrar queries en logs
    properties:
      hibernate:
        format_sql: true  # Formatear SQL
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

### 7.4 Flyway

```yaml
spring:
  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration
    validate-on-migrate: true
```

**Archivos de Migración**:
- `V1__create_initial_schema.sql` - Crea tablas
- `V2__insert_sample_data.sql` - Datos de ejemplo

**Flyway se ejecuta automáticamente** al iniciar la aplicación y crea/actualiza las tablas.

### 7.5 Crear las Tablas Manualmente (Si es necesario)

Si necesitas crear las tablas manualmente:

```bash
# 1. Conectarse a PostgreSQL
docker exec -it eventia-postgres psql -U eventia_user -d eventia_db

# 2. Flyway las creará automáticamente al iniciar la app
# O puedes ejecutar los scripts manualmente:
\i src/main/resources/db/migration/V1__create_initial_schema.sql
\i src/main/resources/db/migration/V2__insert_sample_data.sql
```

---

## 8. Estructura de Carpetas

```
Eventia Core API/
│
├── src/
│   ├── main/
│   │   ├── java/cue/edu/co/eventia_core_api/
│   │   │   │
│   │   │   ├── domain/                          # 🔷 CAPA DE DOMINIO
│   │   │   │   ├── model/                       # Entidades de dominio
│   │   │   │   │   ├── Evento.java             # Entidad Evento
│   │   │   │   │   ├── Participante.java       # Entidad Participante
│   │   │   │   │   ├── Asistencia.java         # Entidad Asistencia
│   │   │   │   │   ├── EstadoEvento.java       # Enum estados de evento
│   │   │   │   │   ├── EstadoAsistencia.java   # Enum estados asistencia
│   │   │   │   │   └── TipoDocumento.java      # Enum tipos documento
│   │   │   │   │
│   │   │   │   ├── repository/                  # Puertos (interfaces)
│   │   │   │   │   ├── EventoRepository.java
│   │   │   │   │   ├── ParticipanteRepository.java
│   │   │   │   │   └── AsistenciaRepository.java
│   │   │   │   │
│   │   │   │   └── service/                     # Servicios de dominio
│   │   │   │       ├── EventoService.java       # Lógica de negocio eventos
│   │   │   │       ├── ParticipanteService.java # Lógica participantes
│   │   │   │       ├── AsistenciaService.java   # Lógica asistencias
│   │   │   │       └── EstadisticasEvento.java  # DTO de estadísticas
│   │   │   │
│   │   │   ├── application/                     # 🔶 CAPA DE APLICACIÓN
│   │   │   │   ├── dto/                         # Data Transfer Objects
│   │   │   │   │   ├── CrearEventoRequest.java
│   │   │   │   │   ├── EventoResponse.java
│   │   │   │   │   ├── CrearParticipanteRequest.java
│   │   │   │   │   ├── ParticipanteResponse.java
│   │   │   │   │   ├── RegistrarAsistenciaRequest.java
│   │   │   │   │   └── AsistenciaResponse.java
│   │   │   │   │
│   │   │   │   └── mapper/                      # Mappers (MapStruct)
│   │   │   │       ├── EventoMapper.java
│   │   │   │       ├── ParticipanteMapper.java
│   │   │   │       └── AsistenciaMapper.java
│   │   │   │
│   │   │   ├── infrastructure/                  # 🔵 CAPA DE INFRAESTRUCTURA
│   │   │   │   │
│   │   │   │   ├── rest/                        # Adaptadores REST
│   │   │   │   │   ├── controller/
│   │   │   │   │   │   ├── EventoController.java
│   │   │   │   │   │   ├── ParticipanteController.java
│   │   │   │   │   │   └── AsistenciaController.java
│   │   │   │   │   │
│   │   │   │   │   └── exception/               # Manejo de errores
│   │   │   │   │       ├── GlobalExceptionHandler.java
│   │   │   │   │       ├── ErrorResponse.java
│   │   │   │   │       └── ValidationErrorResponse.java
│   │   │   │   │
│   │   │   │   ├── persistence/                 # Adaptadores de persistencia
│   │   │   │   │   ├── entity/                  # Entidades JPA
│   │   │   │   │   │   ├── EventoEntity.java
│   │   │   │   │   │   ├── ParticipanteEntity.java
│   │   │   │   │   │   └── AsistenciaEntity.java
│   │   │   │   │   │
│   │   │   │   │   ├── repository/              # Repositorios Spring Data
│   │   │   │   │   │   ├── JpaEventoRepository.java
│   │   │   │   │   │   ├── JpaParticipanteRepository.java
│   │   │   │   │   │   └── JpaAsistenciaRepository.java
│   │   │   │   │   │
│   │   │   │   │   ├── adapter/                 # Implementación de puertos
│   │   │   │   │   │   ├── EventoRepositoryAdapter.java
│   │   │   │   │   │   ├── ParticipanteRepositoryAdapter.java
│   │   │   │   │   │   └── AsistenciaRepositoryAdapter.java
│   │   │   │   │   │
│   │   │   │   │   └── mapper/                  # Mappers JPA
│   │   │   │   │       ├── EventoEntityMapper.java
│   │   │   │   │       ├── ParticipanteEntityMapper.java
│   │   │   │   │       └── AsistenciaEntityMapper.java
│   │   │   │   │
│   │   │   │   └── config/                      # Configuraciones
│   │   │   │       └── RedisConfig.java         # Config de Redis
│   │   │   │
│   │   │   ├── exception/                       # Excepciones de dominio
│   │   │   │   ├── DomainException.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   ├── BusinessRuleException.java
│   │   │   │   └── DuplicateResourceException.java
│   │   │   │
│   │   │   └── EventiaCoreApiApplication.java   # Clase principal
│   │   │
│   │   └── resources/
│   │       ├── application.yml                  # Configuración principal
│   │       └── db/migration/                    # Migraciones Flyway
│   │           ├── V1__create_initial_schema.sql
│   │           └── V2__insert_sample_data.sql
│   │
│   └── test/
│       ├── java/cue/edu/co/eventia_core_api/
│       │   ├── domain/service/
│       │   │   └── EventoServiceTest.java       # Tests unitarios
│       │   ├── infrastructure/rest/controller/
│       │   │   └── EventoControllerIntegrationTest.java
│       │   └── e2e/
│       │       └── EventiaCoreApiE2ETest.java   # Tests E2E
│       └── resources/
│           └── application-test.yml             # Config para tests
│
├── build.gradle                                  # Configuración Gradle
├── settings.gradle
├── docker-compose.yml                           # Orquestación Docker
├── Dockerfile                                   # Imagen Docker
├── dev.sh                                       # Script de desarrollo
├── start-docker.sh                              # Script para Docker
├── check-status.sh                              # Script de verificación
│
└── docs/                                        # Documentación
    ├── README.md                                # Documentación principal
    ├── QUICKSTART.md                            # Guía rápida
    ├── API_EXAMPLES.md                          # Ejemplos de API
    ├── ARCHITECTURE.md                          # Este archivo
    └── TROUBLESHOOTING.md                       # Solución de problemas
```

---

## 9. Patrones de Diseño

### 9.1 Patrón Repository

**Problema**: Necesitamos abstraer el acceso a datos.

**Solución**: Interfaces en el dominio, implementación en infraestructura.

```java
// Domain: Puerto (interfaz)
public interface EventoRepository {
    Evento save(Evento evento);
    Optional<Evento> findById(Long id);
}

// Infrastructure: Adaptador (implementación)
@Component
public class EventoRepositoryAdapter implements EventoRepository {
    private final JpaEventoRepository jpaRepository;
    // Implementación usando Spring Data JPA
}
```

**Beneficios**:
- ✅ Dominio independiente de JPA
- ✅ Fácil cambio de tecnología de persistencia
- ✅ Testeable con mocks

### 9.2 Patrón DTO (Data Transfer Object)

**Problema**: No queremos exponer entidades internas en la API.

**Solución**: DTOs para entrada/salida.

```java
// DTO de entrada (Request)
public class CrearEventoRequest {
    private String nombre;
    private LocalDateTime fechaInicio;
    // Solo campos necesarios para crear
}

// DTO de salida (Response)
public class EventoResponse {
    private Long id;
    private String nombre;
    private Integer cuposDisponibles;  // Campo calculado
    // Solo campos necesarios para mostrar
}
```

**Beneficios**:
- ✅ Controla qué se expone en la API
- ✅ Validaciones específicas de API
- ✅ Versionado de API más fácil

### 9.3 Patrón Mapper

**Problema**: Conversión entre objetos de diferentes capas.

**Solución**: MapStruct para mapeo automático.

```java
@Mapper(componentModel = "spring")
public interface EventoMapper {
    Evento toDomain(CrearEventoRequest request);
    EventoResponse toResponse(Evento evento);
}
```

**Beneficios**:
- ✅ Código generado en compile-time
- ✅ Type-safe
- ✅ Alto rendimiento

### 9.4 Patrón Service Layer

**Problema**: Centralizar lógica de negocio.

**Solución**: Servicios de dominio con @Transactional.

```java
@Service
@Transactional
public class AsistenciaService {
    
    public Asistencia registrarAsistencia(...) {
        // 1. Validar evento
        // 2. Validar participante
        // 3. Validar reglas de negocio
        // 4. Guardar asistencia
        // 5. Actualizar contador
    }
}
```

**Beneficios**:
- ✅ Transacciones automáticas
- ✅ Lógica centralizada
- ✅ Reutilizable

### 9.5 Patrón Builder

**Problema**: Constructores complejos.

**Solución**: Lombok @Builder.

```java
@Builder
public class Evento {
    private String nombre;
    private LocalDateTime fechaInicio;
    // ...
}

// Uso:
Evento evento = Evento.builder()
    .nombre("Conferencia")
    .fechaInicio(LocalDateTime.now())
    .build();
```

### 9.6 Patrón Cache-Aside

**Problema**: Consultas repetitivas a BD.

**Solución**: Spring Cache + Redis.

```java
@Cacheable(value = "eventos", key = "#id")
public Evento obtenerEventoPorId(Long id) {
    return repository.findById(id)
        .orElseThrow(...);
}
```

**Flujo**:
1. Consulta caché
2. Si existe → retorna
3. Si no existe → consulta BD y cachea

---

## 10. Guías de Desarrollo

### 10.1 Agregar Nueva Entidad

**Paso 1**: Crear modelo de dominio
```java
// domain/model/NuevaEntidad.java
@Data
@Builder
public class NuevaEntidad {
    private Long id;
    private String campo;
}
```

**Paso 2**: Crear puerto (interfaz)
```java
// domain/repository/NuevaEntidadRepository.java
public interface NuevaEntidadRepository {
    NuevaEntidad save(NuevaEntidad entidad);
    Optional<NuevaEntidad> findById(Long id);
}
```

**Paso 3**: Crear entidad JPA
```java
// infrastructure/persistence/entity/NuevaEntidadEntity.java
@Entity
@Table(name = "nueva_entidad")
public class NuevaEntidadEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
```

**Paso 4**: Crear migración Flyway
```sql
-- V3__create_nueva_entidad.sql
CREATE TABLE nueva_entidad (
    id BIGSERIAL PRIMARY KEY,
    campo VARCHAR(100)
);
```

**Paso 5**: Crear servicio, DTOs, controller, etc.

### 10.2 Agregar Nuevo Endpoint

**Paso 1**: Crear DTO
```java
public class NuevoRequest {
    @NotBlank
    private String campo;
}
```

**Paso 2**: Agregar método en servicio
```java
public Resultado metodoNuevo(Long id) {
    // Lógica de negocio
}
```

**Paso 3**: Agregar endpoint en controller
```java
@PostMapping("/nuevo")
public ResponseEntity<Resultado> metodoNuevo(@RequestBody NuevoRequest request) {
    // Llamar al servicio
}
```

### 10.3 Agregar Caché

```java
@Cacheable(value = "nombreCache", key = "#parametro")
public Resultado metodo(String parametro) {
    // Se cachea automáticamente
}

@CacheEvict(value = "nombreCache", allEntries = true)
public void actualizar() {
    // Limpia el caché
}
```

### 10.4 Testing

**Test Unitario**:
```java
@ExtendWith(MockitoExtension.class)
class ServicioTest {
    @Mock
    private Repository repository;
    
    @InjectMocks
    private Servicio servicio;
    
    @Test
    void debeHacerAlgo() {
        // Given
        when(repository.findById(1L)).thenReturn(...);
        
        // When
        var resultado = servicio.metodo(1L);
        
        // Then
        assertThat(resultado).isNotNull();
    }
}
```

**Test de Integración**:
```java
@SpringBootTest
@AutoConfigureMockMvc
class ControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void debeCrearRecurso() throws Exception {
        mockMvc.perform(post("/api/v1/recurso")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isCreated());
    }
}
```

---

## 🎓 Mejores Prácticas Implementadas

### Código
- ✅ SOLID principles
- ✅ DRY (Don't Repeat Yourself)
- ✅ Clean Code
- ✅ Inmutabilidad donde es posible
- ✅ Logging apropiado

### Arquitectura
- ✅ Separación de responsabilidades
- ✅ Inversión de dependencias
- ✅ Testabilidad
- ✅ Escalabilidad horizontal

### Base de Datos
- ✅ Migraciones versionadas
- ✅ Índices optimizados
- ✅ Constraints de integridad
- ✅ Transacciones ACID

### API
- ✅ RESTful design
- ✅ Versionado (/api/v1)
- ✅ Códigos HTTP correctos
- ✅ Validaciones robustas
- ✅ Manejo de errores consistente

### Seguridad
- ✅ Análisis estático (SpotBugs)
- ✅ Validación de entrada
- ✅ SQL injection prevention (JPA)
- ✅ Prepared statements automáticos

---

## 📚 Referencias

- **Spring Boot Docs**: https://spring.io/projects/spring-boot
- **Clean Architecture**: https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html
- **PostgreSQL Docs**: https://www.postgresql.org/docs/
- **Redis Docs**: https://redis.io/docs/
- **MapStruct**: https://mapstruct.org/

---

**Última actualización**: Noviembre 2024  
**Versión**: 1.0  
**Autor**: Eventia Team - Universidad del Cauca

