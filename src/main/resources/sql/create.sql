--SCHEMA: finance_tool_shema
DROP SCHEMA IF EXISTS finance_tool_shema ;

CREATE SCHEMA IF NOT EXISTS finance_tool_shema
    AUTHORIZATION postgres;

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Table: finance_tool.users

-- DROP TABLE IF EXISTS finance_tool.users;

CREATE TABLE IF NOT EXISTS finance_tool_shema.users
(
    uuid uuid NOT NULL,
    dt_create bigint NOT NULL,
    dt_update bigint NOT NULL,
    mail character varying(255) COLLATE pg_catalog."default" NOT NULL,
    fio character varying(255) COLLATE pg_catalog."default",
    role character varying(50) COLLATE pg_catalog."default" NOT NULL,
    status character varying(50) COLLATE pg_catalog."default" NOT NULL,
    password character varying(255) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT users_pkey PRIMARY KEY (uuid),
    CONSTRAINT users_mail_key UNIQUE (mail)
)

    TABLESPACE pg_default;

ALTER TABLE IF EXISTS finance_tool_shema.users
    OWNER to postgres;

 --Table: finance_tool.mail_verification

-- DROP TABLE IF EXISTS finance_tool.mail_verification;

CREATE TABLE IF NOT EXISTS finance_tool_shema.mail_verification
(
    uuid uuid NOT NULL,
    user_id uuid NOT NULL,
    code character varying(100) NOT NULL,
    verified boolean NOT NULL DEFAULT false,
    email_count integer NOT NULL DEFAULT 1,
    dt_create bigint NOT NULL,
    dt_verified bigint NULL,
    CONSTRAINT mail_verification_pkey PRIMARY KEY (uuid),
    CONSTRAINT mail_verification_user_id_fkey FOREIGN KEY (user_id)
        REFERENCES finance_tool_shema.users (uuid)
        ON DELETE CASCADE
)


    TABLESPACE pg_default;


ALTER TABLE IF EXISTS finance_tool_shema.mail_verification
    OWNER to postgres;

--SCHEMA: classifier
CREATE SCHEMA IF NOT EXISTS classifier
    AUTHORIZATION postgres;

-- Table: classifier.currency

CREATE TABLE IF NOT EXISTS classifier.currency
(
    uuid uuid NOT NULL,
    dt_create bigint NOT NULL,
    dt_update bigint NOT NULL,
    title character varying(10) COLLATE pg_catalog."default" NOT NULL,
    description character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT currency_pkey PRIMARY KEY (uuid),
    CONSTRAINT currency_title_key UNIQUE (title)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS classifier.currency
    OWNER to postgres;

-- Table: classifier.operation_category

CREATE TABLE IF NOT EXISTS classifier.operation_category
(
    uuid uuid NOT NULL,
    dt_create bigint NOT NULL,
    dt_update bigint NOT NULL,
    title character varying(100) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT operation_category_pkey PRIMARY KEY (uuid),
    CONSTRAINT operation_category_title_key UNIQUE (title)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS classifier.operation_category
    OWNER to postgres;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

INSERT INTO finance_tool_shema.users (
    uuid, dt_create, dt_update, mail, fio, role, status, password
) VALUES (
             gen_random_uuid(),
             extract(epoch from now()) * 1000,
             extract(epoch from now()) * 1000,
             'admin@finance.com',
             'System Administrator',
             'ADMIN',
             'ACTIVE',
             '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.'
         );

-- Insert some default currencies
INSERT INTO classifier.currency (uuid, dt_create, dt_update, title, description) VALUES
(gen_random_uuid(), extract(epoch from now()) * 1000, extract(epoch from now()) * 1000, 'USD', 'Доллар США'),
(gen_random_uuid(), extract(epoch from now()) * 1000, extract(epoch from now()) * 1000, 'EUR', 'Евро'),
(gen_random_uuid(), extract(epoch from now()) * 1000, extract(epoch from now()) * 1000, 'BYN', 'Белорусский рубль'),
(gen_random_uuid(), extract(epoch from now()) * 1000, extract(epoch from now()) * 1000, 'RUB', 'Российский рубль');

-- Insert some default operation categories
INSERT INTO classifier.operation_category (uuid, dt_create, dt_update, title) VALUES
(gen_random_uuid(), extract(epoch from now()) * 1000, extract(epoch from now()) * 1000, 'Автомобиль'),
(gen_random_uuid(), extract(epoch from now()) * 1000, extract(epoch from now()) * 1000, 'Продукты'),
(gen_random_uuid(), extract(epoch from now()) * 1000, extract(epoch from now()) * 1000, 'Развлечения'),
(gen_random_uuid(), extract(epoch from now()) * 1000, extract(epoch from now()) * 1000, 'Здоровье'),
(gen_random_uuid(), extract(epoch from now()) * 1000, extract(epoch from now()) * 1000, 'Коммунальные услуги'),
(gen_random_uuid(), extract(epoch from now()) * 1000, extract(epoch from now()) * 1000, 'Заработная плата'),
(gen_random_uuid(), extract(epoch from now()) * 1000, extract(epoch from now()) * 1000, 'Подарки'),
(gen_random_uuid(), extract(epoch from now()) * 1000, extract(epoch from now()) * 1000, 'Интернет'),
(gen_random_uuid(), extract(epoch from now()) * 1000, extract(epoch from now()) * 1000, 'Одежда');

-- Insert test user for accounts
INSERT INTO finance_tool_shema.users (
    uuid, dt_create, dt_update, mail, fio, role, status, password
) VALUES (
    '11111111-1111-1111-1111-111111111111',
    extract(epoch from now()) * 1000,
    extract(epoch from now()) * 1000,
    'test@finance.com',
    'Test User',
    'USER',
    'ACTIVE',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.'
) ON CONFLICT (uuid) DO NOTHING;

-- Get currency UUIDs for test data
WITH currencies AS (
    SELECT uuid as currency_uuid, title 
    FROM classifier.currency 
    WHERE title IN ('USD', 'EUR', 'BYN')
),
categories AS (
    SELECT uuid as category_uuid, title
    FROM classifier.operation_category
    WHERE title IN ('Продукты', 'Заработная плата', 'Автомобиль', 'Развлечения')
)
-- Insert test accounts
INSERT INTO finance_tool_shema.accounts (
    uuid, dt_create, dt_update, title, description, balance, type, currency, user_id
) 
SELECT 
    '22222222-2222-2222-2222-222222222222',
    extract(epoch from now()) * 1000,
    extract(epoch from now()) * 1000,
    'Основной счет',
    'Основной банковский счет для ежедневных операций',
    1500.00,
    'BANK_ACCOUNT',
    c.currency_uuid,
    '11111111-1111-1111-1111-111111111111'
FROM currencies c
WHERE c.title = 'USD'
UNION ALL
SELECT 
    '33333333-3333-3333-3333-333333333333',
    extract(epoch from now()) * 1000,
    extract(epoch from now()) * 1000,
    'Наличные',
    'Наличные деньги в кошельке',
    250.50,
    'CASH',
    c.currency_uuid,
    '11111111-1111-1111-1111-111111111111'
FROM currencies c
WHERE c.title = 'BYN'
UNION ALL
SELECT 
    '44444444-4444-4444-4444-444444444444',
    extract(epoch from now()) * 1000,
    extract(epoch from now()) * 1000,
    'Сберегательный депозит',
    'Депозит на 12 месяцев под 8% годовых',
    5000.00,
    'BANK_DEPOSIT',
    c.currency_uuid,
    '11111111-1111-1111-1111-111111111111'
FROM currencies c
WHERE c.title = 'EUR';

-- Insert test operations
WITH currencies AS (
    SELECT uuid as currency_uuid, title 
    FROM classifier.currency 
    WHERE title IN ('USD', 'EUR', 'BYN')
),
categories AS (
    SELECT uuid as category_uuid, title
    FROM classifier.operation_category
    WHERE title IN ('Продукты', 'Заработная плата', 'Автомобиль', 'Развлечения')
)
INSERT INTO finance_tool_shema.operations (
    uuid, dt_create, dt_update, date, description, category, value, currency, account_id
)
SELECT 
    gen_random_uuid(),
    extract(epoch from now()) * 1000,
    extract(epoch from now()) * 1000,
    extract(epoch from (now() - interval '5 days')) * 1000,
    'Заработная плата за декабрь',
    cat.category_uuid,
    2000.00,
    cur.currency_uuid,
    '22222222-2222-2222-2222-222222222222'
FROM currencies cur, categories cat
WHERE cur.title = 'USD' AND cat.title = 'Заработная плата'
UNION ALL
SELECT 
    gen_random_uuid(),
    extract(epoch from now()) * 1000,
    extract(epoch from now()) * 1000,
    extract(epoch from (now() - interval '3 days')) * 1000,
    'Покупка продуктов в супермаркете',
    cat.category_uuid,
    -85.30,
    cur.currency_uuid,
    '22222222-2222-2222-2222-222222222222'
FROM currencies cur, categories cat
WHERE cur.title = 'USD' AND cat.title = 'Продукты'
UNION ALL
SELECT 
    gen_random_uuid(),
    extract(epoch from now()) * 1000,
    extract(epoch from now()) * 1000,
    extract(epoch from (now() - interval '2 days')) * 1000,
    'Заправка автомобиля',
    cat.category_uuid,
    -60.00,
    cur.currency_uuid,
    '22222222-2222-2222-2222-222222222222'
FROM currencies cur, categories cat
WHERE cur.title = 'USD' AND cat.title = 'Автомобиль'
UNION ALL
SELECT 
    gen_random_uuid(),
    extract(epoch from now()) * 1000,
    extract(epoch from now()) * 1000,
    extract(epoch from (now() - interval '1 day')) * 1000,
    'Кино и поп-корн',
    cat.category_uuid,
    -25.50,
    cur.currency_uuid,
    '22222222-2222-2222-2222-222222222222'
FROM currencies cur, categories cat
WHERE cur.title = 'USD' AND cat.title = 'Развлечения'
UNION ALL
SELECT 
    gen_random_uuid(),
    extract(epoch from now()) * 1000,
    extract(epoch from now()) * 1000,
    extract(epoch from (now() - interval '4 days')) * 1000,
    'Покупка хлеба и молока',
    cat.category_uuid,
    -15.20,
    cur.currency_uuid,
    '33333333-3333-3333-3333-333333333333'
FROM currencies cur, categories cat
WHERE cur.title = 'BYN' AND cat.title = 'Продукты'
UNION ALL
SELECT 
    gen_random_uuid(),
    extract(epoch from now()) * 1000,
    extract(epoch from now()) * 1000,
    extract(epoch from (now() - interval '7 days')) * 1000,
    'Проценты по депозиту',
    cat.category_uuid,
    33.50,
    cur.currency_uuid,
    '44444444-4444-4444-4444-444444444444'
FROM currencies cur, categories cat
WHERE cur.title = 'EUR' AND cat.title = 'Заработная плата';

-- Table: finance_tool_shema.accounts

CREATE TABLE IF NOT EXISTS finance_tool_shema.accounts
(
    uuid uuid NOT NULL,
    dt_create bigint NOT NULL,
    dt_update bigint NOT NULL,
    title character varying(255) COLLATE pg_catalog."default" NOT NULL,
    description character varying(500) COLLATE pg_catalog."default",
    balance numeric(15,2) NOT NULL DEFAULT 0.00,
    type character varying(50) COLLATE pg_catalog."default" NOT NULL,
    currency uuid NOT NULL,
    user_id uuid NOT NULL,
    CONSTRAINT accounts_pkey PRIMARY KEY (uuid),
    CONSTRAINT accounts_user_id_fkey FOREIGN KEY (user_id)
        REFERENCES finance_tool_shema.users (uuid)
        ON DELETE CASCADE,
    CONSTRAINT accounts_currency_fkey FOREIGN KEY (currency)
        REFERENCES classifier.currency (uuid)
        ON DELETE RESTRICT
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS finance_tool_shema.accounts
    OWNER to postgres;

-- Table: finance_tool_shema.operations

CREATE TABLE IF NOT EXISTS finance_tool_shema.operations
(
    uuid uuid NOT NULL,
    dt_create bigint NOT NULL,
    dt_update bigint NOT NULL,
    date bigint NOT NULL,
    description character varying(500) COLLATE pg_catalog."default",
    category uuid NOT NULL,
    value numeric(15,2) NOT NULL,
    currency uuid NOT NULL,
    account_id uuid NOT NULL,
    CONSTRAINT operations_pkey PRIMARY KEY (uuid),
    CONSTRAINT operations_account_id_fkey FOREIGN KEY (account_id)
        REFERENCES finance_tool_shema.accounts (uuid)
        ON DELETE CASCADE,
    CONSTRAINT operations_category_fkey FOREIGN KEY (category)
        REFERENCES classifier.operation_category (uuid)
        ON DELETE RESTRICT,
    CONSTRAINT operations_currency_fkey FOREIGN KEY (currency)
        REFERENCES classifier.currency (uuid)
        ON DELETE RESTRICT
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS finance_tool_shema.operations
    OWNER to postgres;