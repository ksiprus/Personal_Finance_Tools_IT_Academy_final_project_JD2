-- =============================================================================
-- ПОЛНАЯ ИНИЦИАЛИЗАЦИЯ БАЗЫ ДАННЫХ PERSONAL FINANCE TOOLS
-- =============================================================================
-- Автор: Система управления личными финансами
-- Дата создания: 2024
-- Описание: Единый файл для создания полной структуры базы данных
-- =============================================================================

\echo '============================================================================='
\echo 'Начало инициализации базы данных Personal Finance Tools...'
\echo '============================================================================='

-- =============================================================================
-- 1. СОЗДАНИЕ СХЕМ И РАСШИРЕНИЙ
-- =============================================================================

\echo '1. Создание схем и расширений...'

-- Удаление существующих схем (осторожно!)
DROP SCHEMA IF EXISTS finance_tool_shema CASCADE;
DROP SCHEMA IF EXISTS classifier CASCADE;

-- Создание схем
CREATE SCHEMA IF NOT EXISTS finance_tool_shema
    AUTHORIZATION postgres;

CREATE SCHEMA IF NOT EXISTS classifier
    AUTHORIZATION postgres;

-- Создание необходимых расширений
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Комментарии к схемам
COMMENT ON SCHEMA finance_tool_shema IS 'Основная схема для системы управления личными финансами';
COMMENT ON SCHEMA classifier IS 'Схема для справочников и классификаторов';

\echo '✓ Схемы и расширения созданы успешно'

-- =============================================================================
-- 2. СОЗДАНИЕ СПРАВОЧНЫХ ТАБЛИЦ (КЛАССИФИКАТОРЫ)
-- =============================================================================

\echo '2. Создание справочных таблиц...'

-- Таблица: Валюты
CREATE TABLE IF NOT EXISTS classifier.currency (
    uuid uuid NOT NULL DEFAULT gen_random_uuid(),
    dt_create bigint NOT NULL,
    dt_update bigint NOT NULL,
    title character varying(10) COLLATE pg_catalog."default" NOT NULL,
    description character varying(255) COLLATE pg_catalog."default",
    
    CONSTRAINT currency_pkey PRIMARY KEY (uuid),
    CONSTRAINT currency_title_key UNIQUE (title),
    CONSTRAINT currency_title_not_empty CHECK (length(trim(title)) > 0),
    CONSTRAINT currency_dt_create_positive CHECK (dt_create > 0),
    CONSTRAINT currency_dt_update_positive CHECK (dt_update > 0),
    CONSTRAINT currency_dt_update_gte_create CHECK (dt_update >= dt_create)
) TABLESPACE pg_default;

ALTER TABLE classifier.currency OWNER TO postgres;
COMMENT ON TABLE classifier.currency IS 'Справочник валют';

-- Таблица: Категории операций
CREATE TABLE IF NOT EXISTS classifier.operation_category (
    uuid uuid NOT NULL DEFAULT gen_random_uuid(),
    dt_create bigint NOT NULL,
    dt_update bigint NOT NULL,
    title character varying(100) COLLATE pg_catalog."default" NOT NULL,
    
    CONSTRAINT operation_category_pkey PRIMARY KEY (uuid),
    CONSTRAINT operation_category_title_key UNIQUE (title),
    CONSTRAINT operation_category_title_not_empty CHECK (length(trim(title)) > 0),
    CONSTRAINT operation_category_dt_create_positive CHECK (dt_create > 0),
    CONSTRAINT operation_category_dt_update_positive CHECK (dt_update > 0),
    CONSTRAINT operation_category_dt_update_gte_create CHECK (dt_update >= dt_create)
) TABLESPACE pg_default;

ALTER TABLE classifier.operation_category OWNER TO postgres;
COMMENT ON TABLE classifier.operation_category IS 'Справочник категорий операций';

\echo '✓ Справочные таблицы созданы успешно'

-- =============================================================================
-- 3. СОЗДАНИЕ ОСНОВНЫХ ТАБЛИЦ СИСТЕМЫ
-- =============================================================================

\echo '3. Создание основных таблиц...'

-- Таблица: Пользователи
CREATE TABLE IF NOT EXISTS finance_tool_shema.users (
    uuid uuid NOT NULL DEFAULT gen_random_uuid(),
    dt_create bigint NOT NULL,
    dt_update bigint NOT NULL,
    mail character varying(255) COLLATE pg_catalog."default" NOT NULL,
    fio character varying(255) COLLATE pg_catalog."default",
    role character varying(50) COLLATE pg_catalog."default" NOT NULL,
    status character varying(50) COLLATE pg_catalog."default" NOT NULL,
    password character varying(255) COLLATE pg_catalog."default" NOT NULL,
    
    CONSTRAINT users_pkey PRIMARY KEY (uuid),
    CONSTRAINT users_mail_key UNIQUE (mail),
    CONSTRAINT users_role_check CHECK (role IN ('ADMIN', 'USER', 'MANAGER')),
    CONSTRAINT users_status_check CHECK (status IN ('WAITING_ACTIVATION', 'ACTIVE', 'DEACTIVATED')),
    CONSTRAINT users_mail_not_empty CHECK (length(trim(mail)) > 0),
    CONSTRAINT users_password_not_empty CHECK (length(trim(password)) > 0),
    CONSTRAINT users_dt_create_positive CHECK (dt_create > 0),
    CONSTRAINT users_dt_update_positive CHECK (dt_update > 0),
    CONSTRAINT users_dt_update_gte_create CHECK (dt_update >= dt_create),
    CONSTRAINT users_mail_format CHECK (mail ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
) TABLESPACE pg_default;

ALTER TABLE finance_tool_shema.users OWNER TO postgres;
COMMENT ON TABLE finance_tool_shema.users IS 'Пользователи системы';

-- Таблица: Верификация email
CREATE TABLE IF NOT EXISTS finance_tool_shema.mail_verification (
    uuid uuid NOT NULL DEFAULT gen_random_uuid(),
    user_id uuid NOT NULL,
    mail character varying(255) COLLATE pg_catalog."default" NOT NULL,
    code character varying(100) NOT NULL,
    verified boolean NOT NULL DEFAULT false,
    email_count integer NOT NULL DEFAULT 1,
    dt_create bigint NOT NULL,
    dt_verified bigint NULL,
    
    CONSTRAINT mail_verification_pkey PRIMARY KEY (uuid),
    CONSTRAINT mail_verification_user_id_fkey FOREIGN KEY (user_id)
        REFERENCES finance_tool_shema.users (uuid)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT mail_verification_code_not_empty CHECK (length(trim(code)) > 0),
    CONSTRAINT mail_verification_email_count_positive CHECK (email_count > 0),
    CONSTRAINT mail_verification_dt_create_positive CHECK (dt_create > 0)
) TABLESPACE pg_default;

ALTER TABLE finance_tool_shema.mail_verification OWNER TO postgres;
COMMENT ON TABLE finance_tool_shema.mail_verification IS 'Верификация email адресов пользователей';

-- Таблица: Счета пользователей
CREATE TABLE IF NOT EXISTS finance_tool_shema.accounts (
    uuid uuid NOT NULL DEFAULT gen_random_uuid(),
    dt_create bigint NOT NULL,
    dt_update bigint NOT NULL,
    title character varying(255) COLLATE pg_catalog."default" NOT NULL,
    description character varying(500) COLLATE pg_catalog."default",
    balance numeric(15,2) NOT NULL DEFAULT 0.00,
    type character varying(50) COLLATE pg_catalog."default" NOT NULL,
    currency uuid NOT NULL,
    user_id uuid NOT NULL,
    
    CONSTRAINT accounts_pkey PRIMARY KEY (uuid),
    CONSTRAINT accounts_user_title_unique UNIQUE (user_id, title),
    CONSTRAINT accounts_user_id_fkey FOREIGN KEY (user_id)
        REFERENCES finance_tool_shema.users (uuid)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT accounts_currency_fkey FOREIGN KEY (currency)
        REFERENCES classifier.currency (uuid)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT accounts_type_check CHECK (type IN ('BANK_ACCOUNT', 'CASH', 'BANK_DEPOSIT', 'CARD', 'ELECTRONIC_WALLET')),
    CONSTRAINT accounts_title_not_empty CHECK (length(trim(title)) > 0),
    CONSTRAINT accounts_dt_create_positive CHECK (dt_create > 0),
    CONSTRAINT accounts_dt_update_positive CHECK (dt_update > 0),
    CONSTRAINT accounts_dt_update_gte_create CHECK (dt_update >= dt_create)
) TABLESPACE pg_default;

ALTER TABLE finance_tool_shema.accounts OWNER TO postgres;
COMMENT ON TABLE finance_tool_shema.accounts IS 'Счета пользователей';

-- Таблица: Операции по счетам
CREATE TABLE IF NOT EXISTS finance_tool_shema.operations (
    uuid uuid NOT NULL DEFAULT gen_random_uuid(),
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
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT operations_category_fkey FOREIGN KEY (category)
        REFERENCES classifier.operation_category (uuid)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT operations_currency_fkey FOREIGN KEY (currency)
        REFERENCES classifier.currency (uuid)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT operations_value_not_zero CHECK (value != 0),
    CONSTRAINT operations_date_positive CHECK (date > 0),
    CONSTRAINT operations_dt_create_positive CHECK (dt_create > 0),
    CONSTRAINT operations_dt_update_positive CHECK (dt_update > 0),
    CONSTRAINT operations_dt_update_gte_create CHECK (dt_update >= dt_create)
) TABLESPACE pg_default;

ALTER TABLE finance_tool_shema.operations OWNER TO postgres;
COMMENT ON TABLE finance_tool_shema.operations IS 'Операции по счетам пользователей';

-- Таблица: Аудит действий
CREATE TABLE IF NOT EXISTS finance_tool_shema.audit (
    uuid uuid NOT NULL DEFAULT gen_random_uuid(),
    dt_create bigint NOT NULL,
    dt_update bigint NOT NULL,
    user_uuid uuid NOT NULL,
    text text COLLATE pg_catalog."default" NOT NULL,
    type character varying(50) COLLATE pg_catalog."default" NOT NULL,
    essence_id character varying(255) COLLATE pg_catalog."default" NOT NULL,
    
    CONSTRAINT audit_pkey PRIMARY KEY (uuid),
    CONSTRAINT audit_user_uuid_fkey FOREIGN KEY (user_uuid)
        REFERENCES finance_tool_shema.users (uuid)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT audit_type_check CHECK (type IN ('USER', 'REPORT', 'CURRENCY', 'CATEGORY', 'ACCOUNT', 'OPERATION')),
    CONSTRAINT audit_text_not_empty CHECK (length(trim(text)) > 0),
    CONSTRAINT audit_essence_id_not_empty CHECK (length(trim(essence_id)) > 0),
    CONSTRAINT audit_dt_create_positive CHECK (dt_create > 0),
    CONSTRAINT audit_dt_update_positive CHECK (dt_update > 0),
    CONSTRAINT audit_dt_update_gte_create CHECK (dt_update >= dt_create)
) TABLESPACE pg_default;

ALTER TABLE finance_tool_shema.audit OWNER TO postgres;
COMMENT ON TABLE finance_tool_shema.audit IS 'Аудит действий пользователей в системе';

\echo '✓ Основные таблицы созданы успешно'

-- =============================================================================
-- 4. СОЗДАНИЕ ИНДЕКСОВ
-- =============================================================================

\echo '4. Создание индексов...'

-- Индексы для таблиц users
CREATE INDEX IF NOT EXISTS idx_users_mail ON finance_tool_shema.users(mail);
CREATE INDEX IF NOT EXISTS idx_users_role ON finance_tool_shema.users(role);
CREATE INDEX IF NOT EXISTS idx_users_status ON finance_tool_shema.users(status);

-- Индексы для таблицы mail_verification
CREATE INDEX IF NOT EXISTS idx_mail_verification_user_id ON finance_tool_shema.mail_verification(user_id);
CREATE INDEX IF NOT EXISTS idx_mail_verification_code ON finance_tool_shema.mail_verification(code);
CREATE INDEX IF NOT EXISTS idx_mail_verification_verified ON finance_tool_shema.mail_verification(verified);

-- Индексы для таблицы accounts
CREATE INDEX IF NOT EXISTS idx_accounts_user_id ON finance_tool_shema.accounts(user_id);
CREATE INDEX IF NOT EXISTS idx_accounts_currency ON finance_tool_shema.accounts(currency);
CREATE INDEX IF NOT EXISTS idx_accounts_type ON finance_tool_shema.accounts(type);

-- Индексы для таблицы operations
CREATE INDEX IF NOT EXISTS idx_operations_account_id ON finance_tool_shema.operations(account_id);
CREATE INDEX IF NOT EXISTS idx_operations_category ON finance_tool_shema.operations(category);
CREATE INDEX IF NOT EXISTS idx_operations_currency ON finance_tool_shema.operations(currency);
CREATE INDEX IF NOT EXISTS idx_operations_date ON finance_tool_shema.operations(date DESC);
CREATE INDEX IF NOT EXISTS idx_operations_account_date ON finance_tool_shema.operations(account_id, date DESC);

-- Индексы для таблицы audit
CREATE INDEX IF NOT EXISTS idx_audit_user_uuid ON finance_tool_shema.audit(user_uuid);
CREATE INDEX IF NOT EXISTS idx_audit_type ON finance_tool_shema.audit(type);
CREATE INDEX IF NOT EXISTS idx_audit_dt_create ON finance_tool_shema.audit(dt_create DESC);

\echo '✓ Индексы созданы успешно'

-- =============================================================================
-- 5. ЗАПОЛНЕНИЕ СПРАВОЧНИКОВ
-- =============================================================================

\echo '5. Заполнение справочников...'

-- Вставка валют
INSERT INTO classifier.currency (dt_create, dt_update, title, description) VALUES
(extract(epoch from now()) * 1000, extract(epoch from now()) * 1000, 'USD', 'Доллар США'),
(extract(epoch from now()) * 1000, extract(epoch from now()) * 1000, 'EUR', 'Евро'),
(extract(epoch from now()) * 1000, extract(epoch from now()) * 1000, 'BYN', 'Белорусский рубль'),
(extract(epoch from now()) * 1000, extract(epoch from now()) * 1000, 'RUB', 'Российский рубль'),
(extract(epoch from now()) * 1000, extract(epoch from now()) * 1000, 'PLN', 'Польский злотый'),
(extract(epoch from now()) * 1000, extract(epoch from now()) * 1000, 'UAH', 'Украинская гривна')
ON CONFLICT (title) DO NOTHING;

-- Вставка категорий операций
INSERT INTO classifier.operation_category (dt_create, dt_update, title) VALUES
(extract(epoch from now()) * 1000, extract(epoch from now()) * 1000, 'Продукты питания'),
(extract(epoch from now()) * 1000, extract(epoch from now()) * 1000, 'Транспорт'),
(extract(epoch from now()) * 1000, extract(epoch from now()) * 1000, 'Автомобиль'),
(extract(epoch from now()) * 1000, extract(epoch from now()) * 1000, 'Коммунальные услуги'),
(extract(epoch from now()) * 1000, extract(epoch from now()) * 1000, 'Заработная плата'),
(extract(epoch from now()) * 1000, extract(epoch from now()) * 1000, 'Премии и бонусы'),
(extract(epoch from now()) * 1000, extract(epoch from now()) * 1000, 'Фриланс'),
(extract(epoch from now()) * 1000, extract(epoch from now()) * 1000, 'Прочие доходы'),
(extract(epoch from now()) * 1000, extract(epoch from now()) * 1000, 'Прочие расходы')
ON CONFLICT (title) DO NOTHING;

\echo '✓ Справочники заполнены успешно'

-- =============================================================================
-- 6. СОЗДАНИЕ СИСТЕМНЫХ ПОЛЬЗОВАТЕЛЕЙ
-- =============================================================================

\echo '6. Создание системных пользователей...'

-- Системный администратор
INSERT INTO finance_tool_shema.users (
    dt_create, dt_update, mail, fio, role, status, password
) VALUES (
    extract(epoch from now()) * 1000,
    extract(epoch from now()) * 1000,
    'admin@finance.com',
    'System Administrator',
    'ADMIN',
    'ACTIVE',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.' -- password: password
) ON CONFLICT (mail) DO NOTHING;

-- Менеджер системы
INSERT INTO finance_tool_shema.users (
    dt_create, dt_update, mail, fio, role, status, password
) VALUES (
    extract(epoch from now()) * 1000,
    extract(epoch from now()) * 1000,
    'manager@finance.com',
    'System Manager',
    'MANAGER',
    'ACTIVE',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.' -- password: password
) ON CONFLICT (mail) DO NOTHING;

\echo '✓ Системные пользователи созданы успешно'

\echo '============================================================================='
\echo 'Инициализация базы данных завершена успешно!'
\echo 'База данных Personal Finance Tools готова к работе.'
\echo '============================================================================='