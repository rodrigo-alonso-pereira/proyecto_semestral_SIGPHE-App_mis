-- DML Script for populating the 'sigphe_app_db' database
-- Version: 1.2
-- Author: Rodrigo Pereira Yañez
-- Date: 2025-09-19
-- Description: This script populates the database with initial data for testing
--              and demonstration purposes. It includes catalog data, users,
--              tools, loans, and penalties.

-- Set the schema to work in
SET search_path TO sigphe;

--------------------------------------------------------------------------------
-- 1. Populate Catalog Tables
-- These tables contain static data that defines types and statuses used
-- across the application. The IDs are hardcoded for consistency.
--------------------------------------------------------------------------------

-- penalty_types
INSERT INTO penalty_types (id, name, penalty_factor) OVERRIDING SYSTEM VALUE VALUES
(1, 'Atraso', 1.15),
(2, 'Daño irreparable', 1.0),
(3, 'Reparacion', 0.5)
ON CONFLICT (id) DO NOTHING;

-- penalty_statuses
INSERT INTO penalty_statuses (id, name) OVERRIDING SYSTEM VALUE VALUES
(1, 'Activo'),
(2, 'Pagada'),
(3, 'Anulada')
ON CONFLICT (id) DO NOTHING;

-- tool_statuses
INSERT INTO tool_statuses (id, name) OVERRIDING SYSTEM VALUE VALUES
(1, 'Disponible'),
(2, 'Prestada'),
(3, 'En Reparacion'),
(4, 'Dada de baja')
ON CONFLICT (id) DO NOTHING;

-- tool_categories
INSERT INTO tool_categories (id, name) OVERRIDING SYSTEM VALUE VALUES
(1, 'Herramienta Eléctrica'),
(2, 'Herramienta Manual'),
(3, 'Medición y Nivelación')
ON CONFLICT (id) DO NOTHING;

-- kardex_types
INSERT INTO kardex_types (id, name) OVERRIDING SYSTEM VALUE VALUES
(1, 'Ingreso'),
(2, 'Prestamo'),
(3, 'Devolucion'),
(4, 'Baja'),
(5, 'Reparacion'),
(6, 'Raparada')
ON CONFLICT (id) DO NOTHING;

-- user_types
INSERT INTO user_types (id, name) OVERRIDING SYSTEM VALUE VALUES
(1, 'Cliente'),
(2, 'Trabajador')
ON CONFLICT (id) DO NOTHING;

-- user_statuses
INSERT INTO user_statuses (id, name) OVERRIDING SYSTEM VALUE VALUES
(1, 'Activo'),
(2, 'Con Deuda'),
(3, 'Inactivo')
ON CONFLICT (id) DO NOTHING;

-- loan_statuses
INSERT INTO loan_statuses (id, name) OVERRIDING SYSTEM VALUE VALUES
(1, 'Vigente'),
(2, 'Atrasada'),
(3, 'Finalizado'),
(4, 'Retornado')
ON CONFLICT (id) DO NOTHING;

-- brands and models
INSERT INTO brands (id, name) OVERRIDING SYSTEM VALUE VALUES 
(1, 'DeWalt'),
(2, 'Bosch'),
(3, 'Makita'),
(4, 'Stanley'),
(5, 'Milwaukee'),
(6, 'Black & Decker'),
(7, 'Truper'),
(8, 'Karcher')
ON CONFLICT (id) DO NOTHING;

INSERT INTO models (id, name, brand_id) OVERRIDING SYSTEM VALUE VALUES
-- DeWalt Models
(1, 'DCD777C2', 1),
(2, 'DCF887B', 1),
(7, 'SDS MAX de 19.4J', 1),
(8, 'DWE4011', 1),
(9, 'DCS570B', 1),
(10, 'DWP849X', 1),
-- Bosch Models
(3, 'GSR 12V-300', 2),
(4, 'GDS 18V-400', 2),
(11, 'GSB 13 RE', 2),
(12, 'GWS 9-125', 2),
(13, 'GTL 3', 2),
-- Makita Models
(5, 'XDT131', 3),
(14, 'HR2470', 3),
(15, '9565CVR', 3),
(16, 'LS1040', 3),
-- Stanley Models
(6, 'Global Plus 3m/10 pulgadas', 4),
(17, 'STMT82770', 4),
(18, 'FMST1-75761', 4),
-- Milwaukee Models
(19, '2853-20', 5),
(20, '2767-20', 5),
(21, '2731-20', 5),
-- Black & Decker Models
(22, 'CD121K', 6),
(23, 'BDCMTTS', 6),
-- Truper Models
(24, 'ROTA-6M', 7),
(25, 'PICO-8M', 7),
-- Karcher Models
(26, 'K2 Basic', 8),
(27, 'K3 Follow Me', 8)
ON CONFLICT (id) DO NOTHING;

-- Reset sequences for generated IDs to avoid collisions with hardcoded values
SELECT setval(pg_get_serial_sequence('sigphe.penalty_types', 'id'), COALESCE(max(id), 1)) FROM sigphe.penalty_types;
SELECT setval(pg_get_serial_sequence('sigphe.penalty_statuses', 'id'), COALESCE(max(id), 1)) FROM sigphe.penalty_statuses;
SELECT setval(pg_get_serial_sequence('sigphe.tool_statuses', 'id'), COALESCE(max(id), 1)) FROM sigphe.tool_statuses;
SELECT setval(pg_get_serial_sequence('sigphe.tool_categories', 'id'), COALESCE(max(id), 1)) FROM sigphe.tool_categories;
SELECT setval(pg_get_serial_sequence('sigphe.kardex_types', 'id'), COALESCE(max(id), 1)) FROM sigphe.kardex_types;
SELECT setval(pg_get_serial_sequence('sigphe.user_types', 'id'), COALESCE(max(id), 1)) FROM sigphe.user_types;
SELECT setval(pg_get_serial_sequence('sigphe.user_statuses', 'id'), COALESCE(max(id), 1)) FROM sigphe.user_statuses;
SELECT setval(pg_get_serial_sequence('sigphe.loan_statuses', 'id'), COALESCE(max(id), 1)) FROM sigphe.loan_statuses;
SELECT setval(pg_get_serial_sequence('sigphe.brands', 'id'), COALESCE(max(id), 1)) FROM sigphe.brands;
SELECT setval(pg_get_serial_sequence('sigphe.models', 'id'), COALESCE(max(id), 1)) FROM sigphe.models;


--------------------------------------------------------------------------------
-- 2. Create Users (Trabajadores y Clientes)
--------------------------------------------------------------------------------
INSERT INTO users (id, national_id, name, email, user_status_id, user_type_id) OVERRIDING SYSTEM VALUE VALUES
(1, '11.111.111-1', 'Rodrigo Pereira Yañez', 'rodrigo.pereira@toolrent.com', 1, 2), -- Trabajador
(2, '22.222.222-2', 'Juan Pérez González', 'juan.perez@email.com', 1, 1),           -- Cliente Activo
(3, '33.333.333-3', 'Ana Gómez Rojas', 'ana.gomez@email.com', 1, 1),                -- Cliente Activo
(4, '44.444.444-4', 'Carlos Soto Muñoz', 'carlos.soto@email.com', 1, 1),            -- Cliente Activo
(5, '55.555.555-5', 'María López Flores', 'maria.lopez@email.com', 1, 1),           -- Cliente Activo
(6, '66.666.666-6', 'Pedro Ramírez Silva', 'pedro.ramirez@email.com', 1, 1),        -- Cliente Activo
(7, '77.777.777-7', 'Lucía Fernández Torres', 'lucia.fernandez@email.com', 1, 1),   -- Cliente Activo
(8, '88.888.888-8', 'Diego Martínez Castro', 'diego.martinez@email.com', 1, 1),     -- Cliente Activo
(9, '99.999.999-9', 'Carmen Vargas Leiva', 'carmen.vargas@email.com', 1, 1),        -- Cliente Activo
(10, '10.101.010-1', 'Roberto Silva Núñez', 'roberto.silva@email.com', 1, 1)     ,   -- Cliente Activo
(11, '11.222.333-4', 'Juan Carlos Bodoque', 'juancarlos.bodoque@toolrent.com', 1, 2), -- Trabajador
(12, '12.221.331-5', 'Guaripolo', 'guaripolo@toolrent.com', 1, 2) -- Trabajador
ON CONFLICT (id) DO NOTHING;

INSERT INTO user_phones (user_id, phone_number) OVERRIDING SYSTEM VALUE VALUES
(1, '+56911111111'),
(1, '+56211111112'), -- Rodrigo tiene teléfono adicional
(2, '+56922222222'),
(3, '+56933333333'),
(4, '+56944444444'),
(5, '+56955555555'),
(6, '+56966666666'),
(7, '+56977777777'),
(8, '+56988888888'),
(9, '+56999999999'),
(10, '+56912345678'),
(11, '+56598745846'),
(12, '+56987452145')
ON CONFLICT (phone_number) DO NOTHING;

SELECT setval(pg_get_serial_sequence('sigphe.users', 'id'), COALESCE(max(id), 1)) FROM sigphe.users;


--------------------------------------------------------------------------------
-- 3. Ingress Tools into Inventory and create initial Kardex entries
--------------------------------------------------------------------------------
-- All tools are set to 'Disponible' (status 1) for initial use in the application

-- Tool 1: Taladro Percutor DeWalt
INSERT INTO tools (id, name, replacement_value, rental_value, tool_category_id, tool_status_id, model_id)
OVERRIDING SYSTEM VALUE VALUES (1, 'Taladro Percutor Inalámbrico 20V DeWalt DCD777C2', 150000, 10000, 1, 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO kardex (date_time, quantity, tool_id, kardex_type_id, worker_user_id)
VALUES ('2025-12-08 09:15:00', 1, 1, 1, 1); -- type 1: Ingreso, user 1: Rodrigo

-- Tool 2: Taladro Atornillador Bosch
INSERT INTO tools (id, name, replacement_value, rental_value, tool_category_id, tool_status_id, model_id)
OVERRIDING SYSTEM VALUE VALUES (2, 'Taladro Atornillador 12V Bosch GSR 12V-300', 80000, 7000, 1, 1, 3) ON CONFLICT (id) DO NOTHING;
INSERT INTO kardex (date_time, quantity, tool_id, kardex_type_id, worker_user_id)
VALUES ('2025-12-08 09:30:00', 1, 2, 1, 1);

-- Tool 3: Llave de Impacto DeWalt
INSERT INTO tools (id, name, replacement_value, rental_value, tool_category_id, tool_status_id, model_id)
OVERRIDING SYSTEM VALUE VALUES (3, 'Llave de Impacto 20V DeWalt DCF887B', 250000, 15000, 1, 1, 2) ON CONFLICT (id) DO NOTHING;
INSERT INTO kardex (date_time, quantity, tool_id, kardex_type_id, worker_user_id)
VALUES ('2025-12-08 09:45:00', 1, 3, 1, 1);

-- Tool 4: Huincha de Medir Stanley
INSERT INTO tools (id, name, replacement_value, rental_value, tool_category_id, tool_status_id, model_id)
OVERRIDING SYSTEM VALUE VALUES (4, 'Huincha de Medir 3m Stanley Global Plus', 10000, 2000, 3, 1, 6) ON CONFLICT (id) DO NOTHING;
INSERT INTO kardex (date_time, quantity, tool_id, kardex_type_id, worker_user_id)
VALUES ('2025-12-08 10:00:00', 1, 4, 1, 1);

-- Tool 5: Rotomartillo DeWalt
INSERT INTO tools (id, name, replacement_value, rental_value, tool_category_id, tool_status_id, model_id)
OVERRIDING SYSTEM VALUE VALUES (5, 'Rotomartillo SDS MAX 19.4J DeWalt', 320000, 20000, 1, 1, 7) ON CONFLICT (id) DO NOTHING;
INSERT INTO kardex (date_time, quantity, tool_id, kardex_type_id, worker_user_id)
VALUES ('2025-12-08 10:20:00', 1, 5, 1, 1);

-- Tool 6: Esmeril Angular DeWalt
INSERT INTO tools (id, name, replacement_value, rental_value, tool_category_id, tool_status_id, model_id)
OVERRIDING SYSTEM VALUE VALUES (6, 'Esmeril Angular 4" DeWalt DWE4011', 95000, 8000, 1, 1, 8) ON CONFLICT (id) DO NOTHING;
INSERT INTO kardex (date_time, quantity, tool_id, kardex_type_id, worker_user_id)
VALUES ('2025-12-08 10:45:00', 1, 6, 1, 1);

-- Tool 7: Sierra Circular DeWalt
INSERT INTO tools (id, name, replacement_value, rental_value, tool_category_id, tool_status_id, model_id)
OVERRIDING SYSTEM VALUE VALUES (7, 'Sierra Circular 20V DeWalt DCS570B', 185000, 12000, 1, 1, 9) ON CONFLICT (id) DO NOTHING;
INSERT INTO kardex (date_time, quantity, tool_id, kardex_type_id, worker_user_id)
VALUES ('2025-12-08 11:10:00', 1, 7, 1, 1);

-- Tool 8: Pulidora DeWalt
INSERT INTO tools (id, name, replacement_value, rental_value, tool_category_id, tool_status_id, model_id)
OVERRIDING SYSTEM VALUE VALUES (8, 'Pulidora DeWalt DWP849X', 140000, 9500, 1, 1, 10) ON CONFLICT (id) DO NOTHING;
INSERT INTO kardex (date_time, quantity, tool_id, kardex_type_id, worker_user_id)
VALUES ('2025-12-08 11:30:00', 1, 8, 1, 1);

-- Tool 9: Taladro Bosch GSB
INSERT INTO tools (id, name, replacement_value, rental_value, tool_category_id, tool_status_id, model_id)
OVERRIDING SYSTEM VALUE VALUES (9, 'Taladro Percutor 600W Bosch GSB 13 RE', 115000, 8500, 1, 1, 11) ON CONFLICT (id) DO NOTHING;
INSERT INTO kardex (date_time, quantity, tool_id, kardex_type_id, worker_user_id)
VALUES ('2025-12-08 12:00:00', 1, 9, 1, 1);

-- Tool 10: Esmeril Angular Bosch
INSERT INTO tools (id, name, replacement_value, rental_value, tool_category_id, tool_status_id, model_id)
OVERRIDING SYSTEM VALUE VALUES (10, 'Esmeril Angular 5" Bosch GWS 9-125', 88000, 7500, 1, 1, 12) ON CONFLICT (id) DO NOTHING;
INSERT INTO kardex (date_time, quantity, tool_id, kardex_type_id, worker_user_id)
VALUES ('2025-12-08 13:15:00', 1, 10, 1, 1);

-- Tool 11: Nivel Láser Bosch
INSERT INTO tools (id, name, replacement_value, rental_value, tool_category_id, tool_status_id, model_id)
OVERRIDING SYSTEM VALUE VALUES (11, 'Nivel Láser de 3 Líneas Bosch GTL 3', 175000, 11000, 3, 1, 13) ON CONFLICT (id) DO NOTHING;
INSERT INTO kardex (date_time, quantity, tool_id, kardex_type_id, worker_user_id)
VALUES ('2025-12-08 13:40:00', 1, 11, 1, 1);

-- Tool 12: Llave de Impacto Makita
INSERT INTO tools (id, name, replacement_value, rental_value, tool_category_id, tool_status_id, model_id)
OVERRIDING SYSTEM VALUE VALUES (12, 'Llave de Impacto 18V Makita XDT131', 220000, 14000, 1, 1, 5) ON CONFLICT (id) DO NOTHING;
INSERT INTO kardex (date_time, quantity, tool_id, kardex_type_id, worker_user_id)
VALUES ('2025-12-08 14:00:00', 1, 12, 1, 1);

-- Tool 13: Rotomartillo Makita
INSERT INTO tools (id, name, replacement_value, rental_value, tool_category_id, tool_status_id, model_id)
OVERRIDING SYSTEM VALUE VALUES (13, 'Rotomartillo SDS-Plus 780W Makita HR2470', 195000, 13000, 1, 1, 14) ON CONFLICT (id) DO NOTHING;
INSERT INTO kardex (date_time, quantity, tool_id, kardex_type_id, worker_user_id)
VALUES ('2025-12-08 14:25:00', 1, 13, 1, 1);

-- Tool 14: Esmeril Angular Makita
INSERT INTO tools (id, name, replacement_value, rental_value, tool_category_id, tool_status_id, model_id)
OVERRIDING SYSTEM VALUE VALUES (14, 'Esmeril Angular 5" Makita 9565CVR', 125000, 9000, 1, 1, 15) ON CONFLICT (id) DO NOTHING;
INSERT INTO kardex (date_time, quantity, tool_id, kardex_type_id, worker_user_id)
VALUES ('2025-12-08 14:50:00', 1, 14, 1, 1);

-- Tool 15: Sierra Ingletadora Makita
INSERT INTO tools (id, name, replacement_value, rental_value, tool_category_id, tool_status_id, model_id)
OVERRIDING SYSTEM VALUE VALUES (15, 'Sierra Ingletadora 10" Makita LS1040', 285000, 18000, 1, 1, 16) ON CONFLICT (id) DO NOTHING;
INSERT INTO kardex (date_time, quantity, tool_id, kardex_type_id, worker_user_id)
VALUES ('2025-12-08 15:15:00', 1, 15, 1, 1);

-- Tool 16: Set de Herramientas Stanley
INSERT INTO tools (id, name, replacement_value, rental_value, tool_category_id, tool_status_id, model_id)
OVERRIDING SYSTEM VALUE VALUES (16, 'Set de Herramientas Manuales 92 Piezas Stanley', 65000, 5000, 2, 1, 17) ON CONFLICT (id) DO NOTHING;
INSERT INTO kardex (date_time, quantity, tool_id, kardex_type_id, worker_user_id)
VALUES ('2025-12-08 15:35:00', 1, 16, 1, 1);

-- Tool 17: Caja de Herramientas Stanley
INSERT INTO tools (id, name, replacement_value, rental_value, tool_category_id, tool_status_id, model_id)
OVERRIDING SYSTEM VALUE VALUES (17, 'Caja de Herramientas FatMax Stanley', 45000, 3500, 2, 1, 18) ON CONFLICT (id) DO NOTHING;
INSERT INTO kardex (date_time, quantity, tool_id, kardex_type_id, worker_user_id)
VALUES ('2025-12-08 15:50:00', 1, 17, 1, 1);

-- Tool 18: Llave de Impacto Milwaukee
INSERT INTO tools (id, name, replacement_value, rental_value, tool_category_id, tool_status_id, model_id)
OVERRIDING SYSTEM VALUE VALUES (18, 'Llave de Impacto M18 Milwaukee 2853-20', 265000, 16000, 1, 1, 19) ON CONFLICT (id) DO NOTHING;
INSERT INTO kardex (date_time, quantity, tool_id, kardex_type_id, worker_user_id)
VALUES ('2025-12-08 16:10:00', 1, 18, 1, 1);

-- Tool 19: Llave de Impacto Alta Torsión Milwaukee
INSERT INTO tools (id, name, replacement_value, rental_value, tool_category_id, tool_status_id, model_id)
OVERRIDING SYSTEM VALUE VALUES (19, 'Llave de Impacto 1/2" Milwaukee 2767-20', 340000, 21000, 1, 1, 20) ON CONFLICT (id) DO NOTHING;
INSERT INTO kardex (date_time, quantity, tool_id, kardex_type_id, worker_user_id)
VALUES ('2025-12-08 16:30:00', 1, 19, 1, 1);

-- Tool 20: Sierra SAWZALL Milwaukee
INSERT INTO tools (id, name, replacement_value, rental_value, tool_category_id, tool_status_id, model_id)
OVERRIDING SYSTEM VALUE VALUES (20, 'Sierra Sable M18 Milwaukee 2731-20', 215000, 14500, 1, 1, 21) ON CONFLICT (id) DO NOTHING;
INSERT INTO kardex (date_time, quantity, tool_id, kardex_type_id, worker_user_id)
VALUES ('2025-12-08 16:50:00', 1, 20, 1, 1);

-- Tool 21: Taladro Black & Decker
INSERT INTO tools (id, name, replacement_value, rental_value, tool_category_id, tool_status_id, model_id)
OVERRIDING SYSTEM VALUE VALUES (21, 'Taladro Percutor 12V Black & Decker CD121K', 58000, 5500, 1, 1, 22) ON CONFLICT (id) DO NOTHING;
INSERT INTO kardex (date_time, quantity, tool_id, kardex_type_id, worker_user_id)
VALUES ('2025-12-08 17:10:00', 1, 21, 1, 1);

-- Tool 22: Multiherramienta Black & Decker
INSERT INTO tools (id, name, replacement_value, rental_value, tool_category_id, tool_status_id, model_id)
OVERRIDING SYSTEM VALUE VALUES (22, 'Multiherramienta Oscilante 20V Black & Decker', 92000, 7800, 1, 1, 23) ON CONFLICT (id) DO NOTHING;
INSERT INTO kardex (date_time, quantity, tool_id, kardex_type_id, worker_user_id)
VALUES ('2025-12-08 17:30:00', 1, 22, 1, 1);

-- Tool 23: Rotomartillo Truper
INSERT INTO tools (id, name, replacement_value, rental_value, tool_category_id, tool_status_id, model_id)
OVERRIDING SYSTEM VALUE VALUES (23, 'Rotomartillo 6kg 850W Truper ROTA-6M', 78000, 6500, 1, 1, 24) ON CONFLICT (id) DO NOTHING;
INSERT INTO kardex (date_time, quantity, tool_id, kardex_type_id, worker_user_id)
VALUES ('2025-12-08 17:45:00', 1, 23, 1, 1);

-- Tool 24: Pico Demoledor Truper
INSERT INTO tools (id, name, replacement_value, rental_value, tool_category_id, tool_status_id, model_id)
OVERRIDING SYSTEM VALUE VALUES (24, 'Martillo Demoledor 8kg 1200W Truper PICO-8M', 145000, 11500, 1, 1, 25) ON CONFLICT (id) DO NOTHING;
INSERT INTO kardex (date_time, quantity, tool_id, kardex_type_id, worker_user_id)
VALUES ('2025-12-08 18:00:00', 1, 24, 1, 1);

-- Tool 25: Hidrolavadora Karcher K2
INSERT INTO tools (id, name, replacement_value, rental_value, tool_category_id, tool_status_id, model_id)
OVERRIDING SYSTEM VALUE VALUES (25, 'Hidrolavadora 110 Bar Karcher K2 Basic', 135000, 10000, 1, 1, 26) ON CONFLICT (id) DO NOTHING;
INSERT INTO kardex (date_time, quantity, tool_id, kardex_type_id, worker_user_id)
VALUES ('2025-12-08 18:15:00', 1, 25, 1, 1);

-- Tool 26: Hidrolavadora Karcher K3
INSERT INTO tools (id, name, replacement_value, rental_value, tool_category_id, tool_status_id, model_id)
OVERRIDING SYSTEM VALUE VALUES (26, 'Hidrolavadora 120 Bar Karcher K3 Follow Me', 185000, 13500, 1, 1, 27) ON CONFLICT (id) DO NOTHING;
INSERT INTO kardex (date_time, quantity, tool_id, kardex_type_id, worker_user_id)
VALUES ('2025-12-08 18:30:00', 1, 26, 1, 1);

SELECT setval(pg_get_serial_sequence('sigphe.tools', 'id'), COALESCE(max(id), 1)) FROM sigphe.tools;


--------------------------------------------------------------------------------
-- 4. Additional Setup
--------------------------------------------------------------------------------
-- All tools are available and ready for use in the application.
-- No loans or penalties are created to allow testing from a clean state.

-- Reset loan sequence
SELECT setval(pg_get_serial_sequence('sigphe.loans', 'id'), 100);

-- End of DML Script
-- All data has been loaded successfully.
-- Database is ready for application use.
