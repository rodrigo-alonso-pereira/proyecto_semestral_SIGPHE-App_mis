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
(5, 'Reparacion')
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
(3, 'Finalizado')
ON CONFLICT (id) DO NOTHING;

-- brands and models
INSERT INTO brands (id, name) OVERRIDING SYSTEM VALUE VALUES (1, 'DeWalt'), (2, 'Bosch'), (3, 'Makita') ON CONFLICT (id) DO NOTHING;
INSERT INTO models (id, name, brand_id) OVERRIDING SYSTEM VALUE VALUES
(1, 'DCD777C2', 1), (2, 'DCF887B', 1),
(3, 'GSR 12V-300', 2), (4, 'GDS 18V-400', 2),
(5, 'XDT131', 3)
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
(2, '22.222.222-2', 'Juan Pérez', 'juan.perez@email.com', 1, 1),             -- Cliente Activo
(3, '33.333.333-3', 'Ana Gómez', 'ana.gomez@email.com', 1, 1),                -- Cliente Activo
(4, '44.444.444-4', 'Carlos Soto', 'carlos.soto@email.com', 2, 1)            -- Cliente con Deuda
ON CONFLICT (id) DO NOTHING;

INSERT INTO user_phones (user_id, phone_number) OVERRIDING SYSTEM VALUE VALUES
(1, '+56911111111'),
(2, '+56922222222'),
(3, '+56933333333')
ON CONFLICT (phone_number) DO NOTHING;

SELECT setval(pg_get_serial_sequence('sigphe.users', 'id'), COALESCE(max(id), 1)) FROM sigphe.users;


--------------------------------------------------------------------------------
-- 3. Ingress Tools into Inventory and create initial Kardex entries
--------------------------------------------------------------------------------
-- Tool 1: Taladro DeWalt
INSERT INTO tools (id, name, replacement_value, rental_value, tool_category_id, tool_status_id, model_id)
OVERRIDING SYSTEM VALUE VALUES (1, 'Taladro Percutor Inalámbrico 20V DeWalt', 150000, 10000, 1, 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO kardex (tool_id, kardex_type_id, user_id, quantity)
OVERRIDING SYSTEM VALUE VALUES (1, 1, 1, 1); -- type 1: Ingreso, user 1: Trabajador

-- Tool 2: Taladro Bosch
INSERT INTO tools (id, name, replacement_value, rental_value, tool_category_id, tool_status_id, model_id)
OVERRIDING SYSTEM VALUE VALUES (2, 'Taladro Atornillador 12V Bosch', 80000, 7000, 1, 1, 3) ON CONFLICT (id) DO NOTHING;
INSERT INTO kardex (tool_id, kardex_type_id, user_id, quantity)
OVERRIDING SYSTEM VALUE VALUES (2, 1, 1, 1);

-- Tool 3: Llave de Impacto DeWalt
INSERT INTO tools (id, name, replacement_value, rental_value, tool_category_id, tool_status_id, model_id)
OVERRIDING SYSTEM VALUE VALUES (3, 'Llave de Impacto 20V DeWalt', 250000, 15000, 1, 1, 2) ON CONFLICT (id) DO NOTHING;
INSERT INTO kardex (tool_id, kardex_type_id, user_id, quantity)
OVERRIDING SYSTEM VALUE VALUES (3, 1, 1, 1);

-- Tool 4: Huincha de Medir
INSERT INTO tools (id, name, replacement_value, rental_value, tool_category_id, tool_status_id, model_id)
OVERRIDING SYSTEM VALUE VALUES (4, 'Huincha de Medir 8m', 15000, 2000, 3, 1, 3) ON CONFLICT (id) DO NOTHING;
INSERT INTO kardex (tool_id, kardex_type_id, user_id, quantity)
OVERRIDING SYSTEM VALUE VALUES (4, 1, 1, 1);

SELECT setval(pg_get_serial_sequence('sigphe.tools', 'id'), COALESCE(max(id), 1)) FROM sigphe.tools;


--------------------------------------------------------------------------------
-- 4. Simulate Business Scenarios
--------------------------------------------------------------------------------

-- SCENARIO 1: A valid, current loan for Juan Pérez (Cliente 2)
-- =============================================================
-- Create the loan
INSERT INTO loans (id, start_date, due_date, total_amount, loan_status_id, customer_user_id)
OVERRIDING SYSTEM VALUE VALUES (101, '2025-09-18 10:00:00', '2025-09-22 10:00:00', 17000, 1, 2) ON CONFLICT (id) DO NOTHING; -- status 1: Vigente

-- Add details for the loan
INSERT INTO loan_details (loan_id, tool_id, rental_value_at_time) OVERRIDING SYSTEM VALUE VALUES
(101, 2, 7000),  -- Taladro Bosch
(101, 3, 15000); -- Llave de Impacto DeWalt. Total is rental_value * days, here just an example value.

-- Update tool statuses to 'Prestada'
UPDATE tools SET tool_status_id = 2 WHERE id IN (2, 3); -- status 2: Prestada

-- Create Kardex entries for the loan
INSERT INTO kardex (tool_id, kardex_type_id, user_id, quantity) OVERRIDING SYSTEM VALUE VALUES
(2, 2, 1, -1), -- type 2: Prestamo
(3, 2, 1, -1);


-- SCENARIO 2: An overdue loan for Ana Gómez (Cliente 3) with a penalty
-- =====================================================================
-- Create the loan
INSERT INTO loans (id, start_date, due_date, total_amount, loan_status_id, customer_user_id)
OVERRIDING SYSTEM VALUE VALUES (102, '2025-09-10 15:00:00', '2025-09-15 15:00:00', 10000, 2, 3) ON CONFLICT (id) DO NOTHING; -- status 2: Atrasada

-- Add detail for the loan
INSERT INTO loan_details (loan_id, tool_id, rental_value_at_time) OVERRIDING SYSTEM VALUE VALUES
(102, 1, 10000); -- Taladro DeWalt

-- Update tool status to 'Prestada'
UPDATE tools SET tool_status_id = 2 WHERE id = 1;

-- Create Kardex entry for the loan
INSERT INTO kardex (tool_id, kardex_type_id, user_id, quantity) OVERRIDING SYSTEM VALUE VALUES
(1, 2, 1, -1);

-- Create an active penalty for this overdue loan
INSERT INTO penalties (loan_id, penalty_type_id, penalty_status_id, penalty_amount, description)
OVERRIDING SYSTEM VALUE VALUES (102, 1, 1, 4600, 'Atraso de 4 días. Multa del 15% diario sobre valor arriendo.'); -- type 1: Atraso, status 1: Activo

SELECT setval(pg_get_serial_sequence('sigphe.loans', 'id'), COALESCE(max(id), 1)) FROM sigphe.loans;
