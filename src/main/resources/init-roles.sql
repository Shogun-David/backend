-- Script para insertar roles iniciales en la BD
-- Ejecutar si los roles no existen

INSERT INTO rol (id_rol, nombre, descripcion) 
VALUES (1, 'ADMIN', 'Rol de Administrador');

INSERT INTO rol (id_rol, nombre, descripcion) 
VALUES (2, 'USUARIO', 'Rol de Usuario Regular');

COMMIT;
