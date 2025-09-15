--------------------------------------------------------------------------------
-- Script de Creacion de Modelo de datos para la aplicacion SIGPHE-App
-- Versión: 1.0
-- Motor de BD: PostgreSQL 16.9
-- Alumno: Rodrigo Pereira Yañez
--------------------------------------------------------------------------------

create table tipo_multa (
    id_tipo_multa int generated always as identity,
    nombre varchar(200) not null,
    factor_multa decimal(10,2) not null,
    estado boolean default true,
    constraint tipo_multa_pk primary key (id_tipo_multa),
    constraint tipo_multa_nombre_uk unique (nombre)
);

create table estado_multa (
    id_estado_multa int generated always as identity,
    nombre varchar(200) not null,
    estado boolean default true,
    constraint estado_multa_pk primary key (id_estado_multa),
    constraint estado_multa_nombre_uk unique (nombre)
);

create table marca (
    id_marca int generated always as identity,
    nombre varchar(200) not null,
    estado boolean default true,
    constraint marca_pk primary key (id_marca),
    constraint marca_nombre_uk unique (nombre)
);

create table estado_herramienta (
    id_estado_herramienta int generated always as identity,
    nombre varchar(200) not null,
    estado boolean default true,
    constraint estado_herramienta_pk primary key (id_estado_herramienta),
    constraint estado_herramienta_nombre_uk unique (nombre)
);

create table categoria_herramienta (
    id_categoria_herramienta int generated always as identity,
    nombre varchar(200) not null,
    estado boolean default true,
    constraint categoria_herramienta_pk primary key (id_categoria_herramienta),
    constraint categoria_herramienta_nombre_uk unique (nombre)
);

create table tipo_kardex (
    id_tipo_kardex int generated always as identity,
    nombre varchar(200) not null,
    estado boolean default true,
    constraint tipo_kardex_pk primary key (id_tipo_kardex),
    constraint tipo_kardex_nombre_uk unique (nombre)
);

create table tipo_usuario (
    id_tipo_usuario int generated always as identity,
    nombre varchar(200) not null,
    estado boolean default true,
    constraint tipo_usuario_pk primary key (id_tipo_usuario),
    constraint tipo_usuario_nombre_uk unique (nombre)
);

create table estado_usuario (
    id_estado_usuario int generated always as identity,
    nombre varchar(200) not null,
    estado boolean default true,
    constraint estado_usuario_pk primary key (id_estado_usuario),
    constraint estado_usuario_nombre_uk unique (nombre)
);

create table estado_prestamo (
    id_estado_prestamo int generated always as identity,
    nombre varchar(200) not null,
    estado boolean default true,
    constraint estado_prestamo_pk primary key (id_estado_prestamo),
    constraint estado_prestamo_nombre_uk unique (nombre)
);

create table modelo (
    id_modelo int generated always as identity,
    nombre varchar(200) not null,
    estado boolean default true,
    marca_id int not null,
    constraint modelo_pk primary key (id_modelo),
    constraint modelo_nombre_uk unique (nombre),
    constraint modelo_marca_id_check check (marca_id > 0),
    constraint modelo_marca_id_fk foreign key (marca_id) references marca(id_marca)
);

create table herramienta (
    id_herramienta int generated always as identity,
    nombre varchar(200) not null,
    valor_reposicion numeric(10, 2) not null,
    valor_arriendo numeric(10, 2) not null,
    estado boolean default true,
    categoria_herramienta_id int not null,
    estado_herramienta_id int not null,
    modelo_id int not null,
    constraint herramienta_pk primary key (id_herramienta),
    constraint herramienta_valor_reposicion_check check (valor_reposicion > 0),
    constraint herramienta_valor_arriendo_check check (valor_arriendo > 0),
    constraint herramienta_categoria_herramienta_id_check check (categoria_herramienta_id > 0),
    constraint herramienta_estado_herramienta_id_check check (estado_herramienta_id > 0),
    constraint herramienta_modelo_id_check check (modelo_id > 0),
    constraint herramienta_categoria_herramienta_id_fk foreign key (categoria_herramienta_id) references categoria_herramienta(id_categoria_herramienta),
    constraint herramienta_estado_herramienta_id_fk foreign key (estado_herramienta_id) references estado_herramienta(id_estado_herramienta),
    constraint herramienta_modelo_id_fk foreign key (modelo_id) references modelo(id_modelo)   
);

create table usuario (
    id_usuario int generated always as identity,
    rut varchar(30) not null,
    nombre varchar(200) not null,
    email varchar(200) not null,
    fecha_registro timestamp not null default current_timestamp,
    estado_usuario_id int not null,
    tipo_usuario_id int not null,
    constraint usuario_pk primary key (id_usuario),
    constraint usuario_rut_uk unique (rut),
    constraint usuario_email_uk unique (email),
    constraint usuario_estado_usuario_id_check check (estado_usuario_id > 0),
    constraint usuario_tipo_usuario_id_check check (tipo_usuario_id > 0),
    constraint usuario_estado_usuario_id_fk foreign key (estado_usuario_id) references estado_usuario(id_estado_usuario),
    constraint usuario_tipo_usuario_id_fk foreign key (tipo_usuario_id) references tipo_usuario(id_tipo_usuario)
);

create table telefono_usuario (
    id_telefono_usuario int generated always as identity,
    telefono varchar(20) not null,
    estado boolean default true,
    usuario_id int not null,
    constraint telefono_usuario_pk primary key (id_telefono_usuario),
    constraint telefono_usuario_telefono_uk unique (telefono),
    constraint telefono_usuario_usuario_id_check check (usuario_id > 0),
    constraint telefono_usuario_usuario_id_fk foreign key (usuario_id) references usuario(id_usuario)
);

create table kardex (
    id_kardex int generated always as identity,
    fecha_hora timestamp not null default current_timestamp,
    cantidad int not null,
    herramienta_id int not null,
    tipo_kardex_id int not null,
    usuario_id int not null,
    constraint kardex_pk primary key (id_kardex),
    constraint kardex_herramienta_id_check check (herramienta_id > 0),
    constraint kardex_tipo_kardex_id_check check (tipo_kardex_id > 0),
    constraint kardex_usuario_id_check check (usuario_id > 0),
    constraint kardex_herramienta_id_fk foreign key (herramienta_id) references herramienta(id_herramienta),
    constraint kardex_tipo_kardex_id_fk foreign key (tipo_kardex_id) references tipo_kardex(id_tipo_kardex),
    constraint kardex_usuario_id_fk foreign key (usuario_id) references usuario(id_usuario)
);

create table prestamo (
    id_prestamo int generated always as identity,
    fecha_inicio timestamp not null default current_timestamp,
    fecha_devolucion timestamp,
    fecha_limite_pactada timestamp not null,
    monto_total numeric(10,2) not null,
    estado_prestamo_id int not null,
    usuario_cliente_id int not null,
    constraint prestamo_pk primary key (id_prestamo),
    constraint prestamo_fechas_check check (fecha_limite_pactada >= fecha_inicio AND (fecha_devolucion IS NULL OR fecha_devolucion >= fecha_inicio)),
    constraint prestamo_monto_total_check check (monto_total >= 0),
    constraint prestamo_estado_prestamo_id_check check (estado_prestamo_id > 0),
    constraint prestamo_usuario_cliente_id_check check (usuario_cliente_id > 0),
    constraint prestamo_estado_prestamo_id_fk foreign key (estado_prestamo_id) references estado_prestamo(id_estado_prestamo),
    constraint prestamo_usuario_cliente_id_fk foreign key (usuario_cliente_id) references usuario(id_usuario)
);

create table multa (
    id_multa int generated always as identity,
    monto_multa numeric(10,2) not null,
    fecha_multa timestamp not null default current_timestamp,
    descripcion varchar(500),
    fecha_pago timestamp,
    prestamo_id int not null,
    tipo_multa_id int not null,
    estado_multa_id int not null,
    constraint multa_pk primary key (id_multa),
    constraint multa_monto_multa_check check (monto_multa > 0),
    constraint multa_fecha_pago_check check (fecha_pago IS NULL OR fecha_pago >= fecha_multa),
    constraint multa_prestamo_id_check check (prestamo_id > 0),
    constraint multa_tipo_multa_id_check check (tipo_multa_id > 0),
    constraint multa_estado_multa_id_check check (estado_multa_id > 0),
    constraint multa_prestamo_id_fk foreign key (prestamo_id) references prestamo(id_prestamo),
    constraint multa_tipo_multa_id_fk foreign key (tipo_multa_id) references tipo_multa(id_tipo_multa),
    constraint multa_estado_multa_id_fk foreign key (estado_multa_id) references estado_multa(id_estado_multa)
);

create table detalle_prestamo (
    herramienta_id int not null,
    prestamo_id int not null,
    valor_arriendo_momento numeric(10,2) not null,
    constraint detalle_prestamo_pk primary key (herramienta_id, prestamo_id),
    constraint detalle_prestamo_herramienta_id_check check (herramienta_id > 0),
    constraint detalle_prestamo_prestamo_id_check check (prestamo_id > 0),
    constraint detalle_prestamo_valor_arriendo_momento_check check (valor_arriendo_momento > 0),
    constraint detalle_prestamo_herramienta_id_fk foreign key (herramienta_id) references herramienta(id_herramienta),
    constraint detalle_prestamo_prestamo_id_fk foreign key (prestamo_id) references prestamo(id_prestamo)
);