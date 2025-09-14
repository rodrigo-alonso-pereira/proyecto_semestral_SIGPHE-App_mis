--------------------------------------------------------------------------------
-- Script de Creacion de Modelo de datos para Rent a Car de Springfield
-- Versión: 1.0
-- Motor de BD: PostgreSQL 16.9
-- Alumno: Rodrigo Pereira Yañez
--------------------------------------------------------------------------------

create table tipo_multa (
    id_tipo_multa int generated always as identity,
    nombre varchar(200) not null,
    factor decimal(10,2) not null,
    constraint id_tipo_multa_pk primary key (id_tipo_multa),
    constraint nombre_tipo_multa_unique unique (nombre)
);

