# 🧪 Guía Completa de Testing

## 📋 Tipos de Pruebas Implementadas

El proyecto tiene **3 niveles de pruebas** automatizadas:

---

## 1️⃣ Pruebas Unitarias

### ¿Qué Prueban?
- Servicios individuales con dependencias simuladas (mocks)
- Lógica de negocio aislada
- Sin base de datos real, sin Spring Context

### Herramientas
- **JUnit 5**: Framework de testing
- **Mockito**: Para crear mocks de dependencias
- **AssertJ**: Assertions fluidas

### Ejemplo: EventoServiceTest.java

```java
@ExtendWith(MockitoExtension.class)
class EventoServiceTest {
    
    @Mock
    private EventoRepository repository;
    
    @InjectMocks
    private EventoService service;
    
    @Test
    void debeCrearEventoExitosamente() {
        // Given (Dado)
        Evento evento = crearEventoValido();
        when(repository.save(any())).thenReturn(evento);
        
        // When (Cuando)
        Evento resultado = service.crearEvento(evento);
        
        // Then (Entonces)
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Evento Test");
    }
}
```

### Ejecutar

```bash
# Solo pruebas unitarias
./gradlew test --tests "*Test"

# Con IntelliJ: Click derecho en EventoServiceTest.java → Run
```

---

## 2️⃣ Pruebas de Integración

### ¿Qué Prueban?
- Controladores REST completos
- Integración entre capas (Controller → Service → Repository)
- Base de datos H2 en memoria
- Spring Context completo

### Herramientas
- **Spring Boot Test**: @SpringBootTest
- **MockMvc**: Para simular peticiones HTTP
- **H2 Database**: BD en memoria para tests

### Ejemplo: EventoControllerIntegrationTest.java

```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class EventoControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void debeCrearEventoYRetornar201() throws Exception {
        // Given
        CrearEventoRequest request = crearRequestValido();
        
        // When & Then
        mockMvc.perform(post("/api/v1/eventos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.nombre").value("Evento Test"));
    }
}
```

### Ejecutar

```bash
# Solo pruebas de integración
./gradlew test --tests "*IntegrationTest"
```

---

## 3️⃣ Pruebas End-to-End (E2E)

### ¿Qué Prueban?
- Flujos completos de usuario
- API real funcionando
- Base de datos y Redis reales
- Escenarios del mundo real

### Herramientas
- **REST Assured**: Cliente HTTP para testing
- **Spring Boot**: Servidor HTTP embebido
- **TestContainers** (opcional): Contenedores Docker para tests

### Ejemplo: EventiaCoreApiE2ETest.java

```java
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
class EventiaCoreApiE2ETest {
    
    @LocalServerPort
    private int port;
    
    private static Long eventoId;
    
    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api/v1";
    }
    
    @Test
    @Order(1)
    void e2e_01_crearEvento() {
        eventoId = given()
            .contentType(ContentType.JSON)
            .body(crearEventoRequest())
        .when()
            .post("/eventos")
        .then()
            .statusCode(201)
            .body("nombre", equalTo("Workshop E2E"))
            .extract().path("id");
            
        assertNotNull(eventoId);
    }
    
    @Test
    @Order(2)
    void e2e_02_crearParticipante() {
        // Crear participante y registrarlo en el evento
        // Verificar que el contador de participantes aumenta
    }
}
```

### Ejecutar

```bash
# Solo pruebas E2E
./gradlew test --tests "*E2ETest"
```

---

## 🎯 Ejecutar Todas las Pruebas

### Con Gradle

```bash
# Todas las pruebas
./gradlew test

# Con reporte HTML
./gradlew test
open build/reports/tests/test/index.html

# Con cobertura de código
./gradlew test jacocoTestReport
open build/reports/jacoco/test/html/index.html
```

### Con el Script

```bash
./dev.sh test              # Todas
./dev.sh test-unit         # Solo unitarias
./dev.sh test-integration  # Solo integración
./dev.sh test-e2e          # Solo E2E
./dev.sh coverage          # Con cobertura
```

---

## 📊 Cobertura de Código (JaCoCo)

### Generar Reporte

```bash
./gradlew jacocoTestReport
```

El reporte se genera en: `build/reports/jacoco/test/html/index.html`

### Ver Reporte

```bash
open build/reports/jacoco/test/html/index.html
```

### Objetivo
- **Líneas cubiertas**: >70%
- **Ramas cubiertas**: >60%

---

## 🧪 Pruebas Manuales con cURL

### 1. Health Check

```bash
curl http://localhost:8080/actuator/health
```

### 2. Crear Evento

```bash
curl -X POST http://localhost:8080/api/v1/eventos \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Evento Manual Test",
    "descripcion": "Prueba manual",
    "fechaInicio": "2025-12-15T10:00:00",
    "fechaFin": "2025-12-15T18:00:00",
    "ubicacion": "Popayán",
    "capacidadMaxima": 50
  }'
```

### 3. Obtener Eventos

```bash
curl http://localhost:8080/api/v1/eventos
```

### 4. Crear Participante

```bash
curl -X POST http://localhost:8080/api/v1/participantes \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan",
    "apellido": "Pérez",
    "email": "juan@test.com",
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
    "notas": "Test manual"
  }'
```

### 6. Ver Estadísticas

```bash
curl http://localhost:8080/api/v1/asistencias/evento/1/estadisticas
```

---

## 🔍 Pruebas con Postman

### 1. Importar Colección

Crea una colección con estos endpoints:

**Variables de entorno**:
- `base_url`: `http://localhost:8080`
- `evento_id`: (se guarda automáticamente)
- `participante_id`: (se guarda automáticamente)

### 2. Requests Básicos

#### Crear Evento
```
POST {{base_url}}/api/v1/eventos
Body (JSON):
{
  "nombre": "Evento Postman",
  "descripcion": "Prueba desde Postman",
  "fechaInicio": "2025-12-15T10:00:00",
  "fechaFin": "2025-12-15T18:00:00",
  "ubicacion": "Popayán",
  "capacidadMaxima": 100
}

Test Script (para guardar ID):
pm.test("Status is 201", function() {
    pm.response.to.have.status(201);
    var jsonData = pm.response.json();
    pm.environment.set("evento_id", jsonData.id);
});
```

#### Obtener Eventos
```
GET {{base_url}}/api/v1/eventos
```

#### Obtener Evento por ID
```
GET {{base_url}}/api/v1/eventos/{{evento_id}}
```

---

## 🧰 Herramientas de Testing Adicionales

### 1. HTTPie (Cliente HTTP más amigable)

```bash
# Instalar
brew install httpie

# Ejemplos
http GET localhost:8080/api/v1/eventos

http POST localhost:8080/api/v1/eventos \
  nombre="Evento HTTPie" \
  descripcion="Test" \
  fechaInicio="2025-12-15T10:00:00" \
  fechaFin="2025-12-15T18:00:00" \
  ubicacion="Popayán" \
  capacidadMaxima:=100
```

### 2. JMeter (Pruebas de Carga)

Para probar rendimiento bajo carga:

1. Descargar Apache JMeter
2. Crear Test Plan
3. Configurar HTTP Request Sampler
4. Ejecutar con múltiples threads

### 3. Newman (Ejecutar Postman desde CLI)

```bash
# Instalar
npm install -g newman

# Ejecutar colección
newman run eventia-collection.json -e eventia-env.json
```

---

## ✅ Checklist de Testing

Antes de considerar completo el proyecto:

### Unitarios
- [ ] Todos los servicios tienen tests
- [ ] Cobertura >70% en lógica de negocio
- [ ] Tests de casos exitosos
- [ ] Tests de casos de error
- [ ] Tests de validaciones

### Integración
- [ ] Todos los endpoints REST tienen tests
- [ ] Tests con datos válidos (200/201)
- [ ] Tests con datos inválidos (400)
- [ ] Tests de recursos no encontrados (404)
- [ ] Tests de reglas de negocio (422)

### E2E
- [ ] Flujo completo de creación de evento
- [ ] Flujo completo de registro de participante
- [ ] Flujo completo de asistencia
- [ ] Tests de reglas de negocio (doble registro, cupos)

### Manuales
- [ ] API responde correctamente
- [ ] Base de datos se crea automáticamente
- [ ] Redis funciona (caché)
- [ ] Logs son informativos
- [ ] Errores se manejan correctamente

---

## 📈 Métricas de Calidad

### Objetivo
- **Cobertura de líneas**: >70%
- **Cobertura de ramas**: >60%
- **Tests unitarios**: >30
- **Tests de integración**: >10
- **Tests E2E**: >5

### Verificar

```bash
./gradlew test jacocoTestReport

# Ver reporte
open build/reports/jacoco/test/html/index.html
```

---

## 🎓 Buenas Prácticas de Testing

### 1. AAA Pattern (Arrange-Act-Assert)

```java
@Test
void debeCrearEvento() {
    // Arrange (Preparar)
    Evento evento = crearEventoValido();
    
    // Act (Actuar)
    Evento resultado = service.crearEvento(evento);
    
    // Assert (Verificar)
    assertThat(resultado).isNotNull();
}
```

### 2. Nombres Descriptivos

```java
// ✅ BUENO
void debeCrearEventoCuandoDatosValidos()
void debeLanzarExcepcionCuandoCapacidadNegativa()

// ❌ MALO
void test1()
void testCreate()
```

### 3. Tests Independientes

```java
// ✅ BUENO: Cada test limpia después
@AfterEach
void tearDown() {
    repository.deleteAll();
}

// ❌ MALO: Tests dependen unos de otros
```

### 4. Tests Rápidos

- Unitarios: <100ms cada uno
- Integración: <500ms cada uno
- E2E: <2s cada uno

---

## 🚀 Comandos Rápidos

```bash
# Tests
./gradlew test                          # Todas
./gradlew test --tests "*Test"          # Unitarias
./gradlew test --tests "*IntegrationTest" # Integración
./gradlew test --tests "*E2ETest"       # E2E

# Cobertura
./gradlew jacocoTestReport

# Calidad
./gradlew check

# Continuous testing (ejecuta al cambiar código)
./gradlew test --continuous
```

---

**¡Las pruebas son tu red de seguridad!** 🛡️

Siempre ejecuta `./gradlew test` antes de hacer commit.

