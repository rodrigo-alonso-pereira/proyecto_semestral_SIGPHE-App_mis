--------------------------------------------------------------------------------
-- Data Model Creation Script for the SIGPHE-App
-- Version: 1.1
-- DB Engine: PostgreSQL 16.9
-- Author: Rodrigo Pereira Yañez
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------
-- Instructions for Generic SQL Clients (DBeaver, pgAdmin, etc.)
--
-- 1. Connect to a default database (e.g., 'postgres').
-- 2. Manually execute the following command in a query window:
--    CREATE DATABASE sigphe_app_db;
-- 3. Disconnect and open a NEW connection to the 'sigphe_app_db' database.
-- 4. Run the rest of this script in the new connection.
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------
-- Schema Creation
--------------------------------------------------------------------------------
CREATE SCHEMA IF NOT EXISTS sigphe;
SET search_path TO sigphe;

--------------------------------------------------------------------------------
-- Drop existing tables to avoid conflicts during creation.
-- The 'CASCADE' option ensures that dependent objects are also removed.
--------------------------------------------------------------------------------
DROP TABLE IF EXISTS loan_details CASCADE;
DROP TABLE IF EXISTS penalties CASCADE;
DROP TABLE IF EXISTS loans CASCADE;
DROP TABLE IF EXISTS kardex CASCADE;
DROP TABLE IF EXISTS user_phones CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS tools CASCADE;
DROP TABLE IF EXISTS models CASCADE;
DROP TABLE IF EXISTS loan_statuses CASCADE;
DROP TABLE IF EXISTS user_statuses CASCADE;
DROP TABLE IF EXISTS user_types CASCADE;
DROP TABLE IF EXISTS kardex_types CASCADE;
DROP TABLE IF EXISTS tool_categories CASCADE;
DROP TABLE IF EXISTS tool_statuses CASCADE;
DROP TABLE IF EXISTS brands CASCADE;
DROP TABLE IF EXISTS penalty_statuses CASCADE;
DROP TABLE IF EXISTS penalty_types CASCADE; 

--------------------------------------------------------------------------------
-- Data Model Creation Script for the SIGPHE-App

create table penalty_types (
    id bigint generated always as identity,
    name varchar(200) not null,
    penalty_factor decimal(10,2) not null,
    status boolean default true,
    constraint penalty_types_pk primary key (id),
    constraint penalty_types_name_uk unique (name)
);

create table penalty_statuses (
    id bigint generated always as identity,
    name varchar(200) not null,
    status boolean default true,
    constraint penalty_statuses_pk primary key (id),
    constraint penalty_statuses_name_uk unique (name)
);

create table brands (
    id bigint generated always as identity,
    name varchar(200) not null,
    status boolean default true,
    constraint brands_pk primary key (id),
    constraint brands_name_uk unique (name)
);

create table tool_statuses (
    id bigint generated always as identity,
    name varchar(200) not null,
    status boolean default true,
    constraint tool_statuses_pk primary key (id),
    constraint tool_statuses_name_uk unique (name)
);

create table tool_categories (
    id bigint generated always as identity,
    name varchar(200) not null,
    status boolean default true,
    constraint tool_categories_pk primary key (id),
    constraint tool_categories_name_uk unique (name)
);

create table kardex_types (
    id bigint generated always as identity,
    name varchar(200) not null,
    status boolean default true,
    constraint kardex_types_pk primary key (id),
    constraint kardex_types_name_uk unique (name)
);

create table user_types (
    id bigint generated always as identity,
    name varchar(200) not null,
    status boolean default true,
    constraint user_types_pk primary key (id),
    constraint user_types_name_uk unique (name)
);

create table user_statuses (
    id bigint generated always as identity,
    name varchar(200) not null,
    status boolean default true,
    constraint user_statuses_pk primary key (id),
    constraint user_statuses_name_uk unique (name)
);

create table loan_statuses (
    id bigint generated always as identity,
    name varchar(200) not null,
    status boolean default true,
    constraint loan_statuses_pk primary key (id),
    constraint loan_statuses_name_uk unique (name)
);

create table models (
    id bigint generated always as identity,
    name varchar(200) not null,
    status boolean default true,
    brand_id bigint not null,
    constraint models_pk primary key (id),
    constraint models_name_uk unique (name),
    constraint models_brand_id_check check (brand_id > 0),
    constraint models_brand_id_fk foreign key (brand_id) references brands(id)
);

create table tools (
    id bigint generated always as identity,
    name varchar(200) not null,
    replacement_value numeric(10, 2) not null,
    rental_value numeric(10, 2) not null,
    tool_category_id bigint not null,
    tool_status_id bigint not null,
    model_id bigint not null,
    constraint tools_pk primary key (id),
    constraint tools_replacement_value_check check (replacement_value > 0),
    constraint tools_rental_value_check check (rental_value > 0),
    constraint tools_tool_category_id_check check (tool_category_id > 0),
    constraint tools_tool_status_id_check check (tool_status_id > 0),
    constraint tools_model_id_check check (model_id > 0),
    constraint tools_tool_category_id_fk foreign key (tool_category_id) references tool_categories(id),
    constraint tools_tool_status_id_fk foreign key (tool_status_id) references tool_statuses(id),
    constraint tools_model_id_fk foreign key (model_id) references models(id)   
);

create table users (
    id bigint generated always as identity,
    national_id varchar(30) not null,
    name varchar(200) not null,
    email varchar(200) not null,
    registration_date timestamp not null default current_timestamp,
    user_status_id bigint not null,
    user_type_id bigint not null,
    constraint users_pk primary key (id),
    constraint users_national_id_uk unique (national_id),
    constraint users_email_uk unique (email),
    constraint users_user_status_id_check check (user_status_id > 0),
    constraint users_user_type_id_check check (user_type_id > 0),
    constraint users_user_status_id_fk foreign key (user_status_id) references user_statuses(id),
    constraint users_user_type_id_fk foreign key (user_type_id) references user_types(id)
);

create table user_phones (
    id bigint generated always as identity,
    phone_number varchar(20) not null,
    status boolean default true,
    user_id bigint not null,
    constraint user_phones_pk primary key (id),
    constraint user_phones_phone_number_uk unique (phone_number),
    constraint user_phones_user_id_check check (user_id > 0),
    constraint user_phones_user_id_fk foreign key (user_id) references users(id)
);

create table kardex (
    id bigint generated always as identity,
    date_time timestamp not null default current_timestamp,
    quantity int not null,
    tool_id bigint not null,
    kardex_type_id bigint not null,
    user_id bigint not null,
    constraint kardex_pk primary key (id),
    constraint kardex_tool_id_check check (tool_id > 0),
    constraint kardex_kardex_type_id_check check (kardex_type_id > 0),
    constraint kardex_user_id_check check (user_id > 0),
    constraint kardex_tool_id_fk foreign key (tool_id) references tools(id),
    constraint kardex_kardex_type_id_fk foreign key (kardex_type_id) references kardex_types(id),
    constraint kardex_user_id_fk foreign key (user_id) references users(id)
);

create table loans (
    id bigint generated always as identity,
    start_date timestamp not null default current_timestamp,
    return_date timestamp,
    due_date timestamp not null,
    total_amount numeric(10,2) not null,
    loan_status_id bigint not null,
    customer_user_id bigint not null,
    constraint loans_pk primary key (id),
    constraint loans_dates_check check (due_date >= start_date AND (return_date IS NULL OR return_date >= start_date)),
    constraint loans_total_amount_check check (total_amount >= 0),
    constraint loans_loan_status_id_check check (loan_status_id > 0),
    constraint loans_customer_user_id_check check (customer_user_id > 0),
    constraint loans_loan_status_id_fk foreign key (loan_status_id) references loan_statuses(id),
    constraint loans_customer_user_id_fk foreign key (customer_user_id) references users(id)
);

create table penalties (
    id bigint generated always as identity,
    penalty_amount numeric(10,2) not null,
    penalty_date timestamp not null default current_timestamp,
    description varchar(500),
    payment_date timestamp,
    loan_id bigint not null,
    penalty_type_id bigint not null,
    penalty_status_id bigint not null,
    constraint penalties_pk primary key (id),
    constraint penalties_penalty_amount_check check (penalty_amount > 0),
    constraint penalties_payment_date_check check (payment_date IS NULL OR payment_date >= penalty_date),
    constraint penalties_loan_id_check check (loan_id > 0),
    constraint penalties_penalty_type_id_check check (penalty_type_id > 0),
    constraint penalties_penalty_status_id_check check (penalty_status_id > 0),
    constraint penalties_loan_id_fk foreign key (loan_id) references loans(id),
    constraint penalties_penalty_type_id_fk foreign key (penalty_type_id) references penalty_types(id),
    constraint penalties_penalty_status_id_fk foreign key (penalty_status_id) references penalty_statuses(id)
);

create table loan_details (
    tool_id bigint not null,
    loan_id bigint not null,
    rental_value_at_time numeric(10,2) not null,
    constraint loan_details_pk primary key (tool_id, loan_id),
    constraint loan_details_tool_id_check check (tool_id > 0),
    constraint loan_details_loan_id_check check (loan_id > 0),
    constraint loan_details_rental_value_at_time_check check (rental_value_at_time > 0),
    constraint loan_details_tool_id_fk foreign key (tool_id) references tools(id),
    constraint loan_details_loan_id_fk foreign key (loan_id) references loans(id)
);
