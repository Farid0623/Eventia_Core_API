-- Datos de prueba para eventos
INSERT INTO eventos (nombre, descripcion, fecha_inicio, fecha_fin, ubicacion, capacidad_maxima, participantes_registrados, estado) VALUES
('Conferencia de Tecnología 2025', 'Evento anual sobre las últimas tendencias en tecnología', '2025-12-01 09:00:00', '2025-12-01 18:00:00', 'Centro de Convenciones Popayán', 200, 0, 'ACTIVO'),
('Workshop de Spring Boot', 'Taller práctico sobre desarrollo con Spring Boot', '2025-11-20 14:00:00', '2025-11-20 18:00:00', 'Universidad del Cauca - Sala 301', 50, 0, 'ACTIVO'),
('Meetup de Java Developers', 'Encuentro mensual de desarrolladores Java', '2025-11-25 18:00:00', '2025-11-25 21:00:00', 'Hub de Innovación', 30, 0, 'ACTIVO');

-- Datos de prueba para participantes
INSERT INTO participantes (nombre, apellido, email, telefono, documento, tipo_documento) VALUES
('Juan', 'Pérez', 'juan.perez@email.com', '3001234567', '1234567890', 'CEDULA_CIUDADANIA'),
('María', 'González', 'maria.gonzalez@email.com', '3009876543', '0987654321', 'CEDULA_CIUDADANIA'),
('Carlos', 'Rodríguez', 'carlos.rodriguez@email.com', '3005551234', '5555555555', 'CEDULA_CIUDADANIA'),
('Ana', 'Martínez', 'ana.martinez@email.com', '3007778888', '8888888888', 'CEDULA_CIUDADANIA'),
('Luis', 'López', 'luis.lopez@email.com', '3002223333', '3333333333', 'CEDULA_CIUDADANIA');

