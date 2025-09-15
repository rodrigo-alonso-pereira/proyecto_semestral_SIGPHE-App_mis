--------------------------------------------------------------------------------
-- Script de Creacion de Modelo de datos para Rent a Car de Springfield
-- Versión: 1.0
-- Motor de BD: PostgreSQL 16.9
-- Alumno: Rodrigo Pereira Yañez
--------------------------------------------------------------------------------

create table tipo_multa (
    id_tipo_multa int generated always as identity,
    nombre varchar(200) not null,
    factor_multa decimal(10,2) not null,
    estado boolean default true,
    constraint id_tipo_multa_pk primary key (id_tipo_multa),
    constraint nombre_tipo_multa_unique unique (nombre)
);

create table estado_multa (
    id_estado_multa int generated always as identity,
    nombre varchar(200) not null,
    estado boolean default true,
    constraint id_estado_multa_pk primary key (id_estado_multa),
    constraint nombre_estado_multa_unique unique (nombre)
);

create table marca (
    id_marca int generated always as identity,
    nombre varchar(200) not null,
    estado boolean default true,
    constraint id_marca_pk primary key (id_marca),
    constraint nombre_marca_unique unique (nombre)
);

create table estado_herramienta (
    id_estado_herramienta int generated always as identity,
    nombre varchar(200) not null,
    estado boolean default true,
    constraint id_estado_herramienta_pk primary key (id_estado_herramienta),
    constraint nombre_estado_herramienta_unique unique (nombre)
);

create table categoria_herramienta (
    id_categoria_herramienta int generated always as identity,
    nombre varchar(200) not null,
    estado boolean default true,
    constraint id_categoria_herramienta_pk primary key (id_categoria_herramienta),
    constraint nombre_categoria_herramienta_unique unique (nombre)
);

create table tipo_kardex (
    id_tipo_kardex int generated always as identity,
    nombre varchar(200) not null,
    estado boolean default true,
    constraint id_tipo_kardex_pk primary key (id_tipo_kardex),
    constraint nombre_tipo_kardex_unique unique (nombre)
);

create table tipo_usuario (
    id_tipo_usuario int generated always as identity,
    nombre varchar(200) not null,
    estado boolean default true,
    constraint id_tipo_usuario_pk primary key (id_tipo_usuario),
    constraint nombre_tipo_usuario_unique unique (nombre)
);

create table estado_usuario (
    id_estado_usuario int generated always as identity,
    nombre varchar(200) not null,
    estado boolean default true,
    constraint id_estado_usuario_pk primary key (id_estado_usuario),
    constraint nombre_estado_usuario_unique unique (nombre)
);

create table estado_prestamo (
    id_estado_prestamo int generated always as identity,
    nombre varchar(200) not null,
    estado boolean default true,
    constraint id_estado_prestamo_pk primary key (id_estado_prestamo),
    constraint nombre_estado_prestamo_unique unique (nombre)
);

create table modelo (
    id_modelo int generated always as identity,
    nombre varchar(200) not null,
    estado boolean default true,
    marca_id int not null,
    constraint id_modelo_pk primary key (id_modelo),
    constraint nombre_modelo_unique unique (nombre),
    constraint modelo_marca_fk foreign key (marca_id) references marca(id_marca)
);

create table herramienta (
    id_herramienta int generated always as identity,
    nombre varchar(200) not null,
    valor_reposicion int not null,
    valor_arriendo int not null,
    estado boolean default true,
    categoria_herramienta_id int not null,
    estado_herramienta_id int not null,
    modelo_id int not null,
    constraint id_herramienta_pk primary key (id_herramienta),
    constraint valor_reposicion_positive check (valor_reposicion > 0),
    constraint valor_arriendo_positive check (valor_arriendo > 0),
    constraint nombre_herramienta_unique unique (nombre),
    constraint categoria_herramienta_fk foreign key (categoria_herramienta_id) references categoria_herramienta(id_categoria_herramienta),
    constraint estado_herramienta_fk foreign key (estado_herramienta_id) references estado_herramienta(id_estado_herramienta),
    constraint modelo_herramienta_fk foreign key (modelo_id) references modelo(id_modelo)   
);

create table usuario (
    id_usuario int generated always as identity,
    rut varchar(30) not null,
    nombre varchar(200) not null,
    email varchar(200) not null,
    fecha_registro timestamp default current_timestamp,
    estado_usuario_id int not null,
    tipo_usuario_id int not null,
    constraint id_usuario_pk primary key (id_usuario),
    constraint rut_usuario_unique unique (rut),
    constraint email_usuario_unique unique (email),
    constraint estado_usuario_fk foreign key (estado_usuario_id) references estado_usuario(id_estado_usuario),
    constraint tipo_usuario_fk foreign key (tipo_usuario_id) references tipo_usuario(id_tipo_usuario)
);

create table telefono_

