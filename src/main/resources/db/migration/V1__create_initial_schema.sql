-- Creación de tabla eventos
CREATE TABLE eventos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT,
    fecha_inicio TIMESTAMP NOT NULL,
    fecha_fin TIMESTAMP NOT NULL,
    ubicacion VARCHAR(300) NOT NULL,
    capacidad_maxima INTEGER NOT NULL,
    participantes_registrados INTEGER NOT NULL DEFAULT 0,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP,
    CONSTRAINT chk_capacidad_positiva CHECK (capacidad_maxima > 0),
    CONSTRAINT chk_participantes_validos CHECK (participantes_registrados >= 0 AND participantes_registrados <= capacidad_maxima),
    CONSTRAINT chk_fechas_validas CHECK (fecha_fin > fecha_inicio)
);

-- Índices para eventos
CREATE INDEX idx_evento_estado ON eventos(estado);
CREATE INDEX idx_evento_fecha_inicio ON eventos(fecha_inicio);

-- Creación de tabla participantes
CREATE TABLE participantes (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    telefono VARCHAR(20),
    documento VARCHAR(50) NOT NULL UNIQUE,
    tipo_documento VARCHAR(30) NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP,
    CONSTRAINT uk_participante_email UNIQUE (email),
    CONSTRAINT uk_participante_documento UNIQUE (documento)
);

-- Índices para participantes
CREATE INDEX idx_participante_email ON participantes(email);
CREATE INDEX idx_participante_documento ON participantes(documento);

-- Creación de tabla asistencias (relación muchos a muchos)
CREATE TABLE asistencias (
    id BIGSERIAL PRIMARY KEY,
    evento_id BIGINT NOT NULL,
    participante_id BIGINT NOT NULL,
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) NOT NULL DEFAULT 'CONFIRMADO',
    notas TEXT,
    fecha_actualizacion TIMESTAMP,
    CONSTRAINT fk_asistencia_evento FOREIGN KEY (evento_id) REFERENCES eventos(id) ON DELETE CASCADE,
    CONSTRAINT fk_asistencia_participante FOREIGN KEY (participante_id) REFERENCES participantes(id) ON DELETE CASCADE,
    CONSTRAINT uk_asistencia_evento_participante UNIQUE (evento_id, participante_id)
);

-- Índices para asistencias
CREATE INDEX idx_asistencia_evento ON asistencias(evento_id);
CREATE INDEX idx_asistencia_participante ON asistencias(participante_id);
CREATE INDEX idx_asistencia_estado ON asistencias(estado);

-- Comentarios para documentación
COMMENT ON TABLE eventos IS 'Tabla que almacena la información de los eventos';
COMMENT ON TABLE participantes IS 'Tabla que almacena la información de los participantes';
COMMENT ON TABLE asistencias IS 'Tabla intermedia que relaciona eventos con participantes';

COMMENT ON COLUMN eventos.capacidad_maxima IS 'Número máximo de participantes permitidos';
COMMENT ON COLUMN eventos.participantes_registrados IS 'Número actual de participantes registrados';
COMMENT ON COLUMN asistencias.estado IS 'Estado: CONFIRMADO, CANCELADO, ASISTIO, NO_ASISTIO, EN_ESPERA';

