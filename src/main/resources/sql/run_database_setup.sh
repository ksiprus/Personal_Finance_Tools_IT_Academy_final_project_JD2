#!/bin/bash

# =============================================================================
# СКРИПТ ИНИЦИАЛИЗАЦИИ БАЗЫ ДАННЫХ PERSONAL FINANCE TOOLS
# =============================================================================
# Описание: Автоматическая инициализация базы данных PostgreSQL
# Автор: Personal Finance Tools Team
# Версия: 1.0
# =============================================================================

# Цвета для вывода
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Функция для вывода сообщений
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Заголовок
echo "============================================================================="
echo "         ИНИЦИАЛИЗАЦИЯ БАЗЫ ДАННЫХ PERSONAL FINANCE TOOLS"
echo "============================================================================="
echo ""

# Получение текущей директории скрипта
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SQL_FILE="$SCRIPT_DIR/complete_database_setup.sql"

print_info "Текущая директория: $SCRIPT_DIR"
print_info "SQL файл: $SQL_FILE"

# Проверка существования SQL файла
if [ ! -f "$SQL_FILE" ]; then
    print_error "Файл complete_database_setup.sql не найден!"
    print_error "Ожидаемый путь: $SQL_FILE"
    exit 1
fi

print_success "SQL файл найден"

# Параметры по умолчанию
DEFAULT_USER="postgres"
DEFAULT_HOST="localhost"
DEFAULT_PORT="5432"
DEFAULT_DATABASE="finance_tool"

# Получение параметров подключения
echo ""
print_info "Настройка подключения к базе данных:"
echo ""

read -p "Пользователь PostgreSQL [$DEFAULT_USER]: " DB_USER
DB_USER=${DB_USER:-$DEFAULT_USER}

read -p "Хост [$DEFAULT_HOST]: " DB_HOST
DB_HOST=${DB_HOST:-$DEFAULT_HOST}

read -p "Порт [$DEFAULT_PORT]: " DB_PORT
DB_PORT=${DB_PORT:-$DEFAULT_PORT}

read -p "Название базы данных [$DEFAULT_DATABASE]: " DB_NAME
DB_NAME=${DB_NAME:-$DEFAULT_DATABASE}

echo ""
print_info "Параметры подключения:"
print_info "  Пользователь: $DB_USER"
print_info "  Хост: $DB_HOST"
print_info "  Порт: $DB_PORT"
print_info "  База данных: $DB_NAME"
echo ""

# Предупреждение о безопасности
print_warning "ВНИМАНИЕ!"
print_warning "Этот скрипт удалит все существующие данные в схемах:"
print_warning "  - finance_tool_shema"
print_warning "  - classifier"
echo ""
read -p "Вы уверены, что хотите продолжить? (да/нет): " CONFIRM

if [[ ! "$CONFIRM" =~ ^(да|yes|y|Да|ДА)$ ]]; then
    print_info "Операция отменена пользователем"
    exit 0
fi

# Проверка подключения к PostgreSQL
print_info "Проверка подключения к PostgreSQL..."

if ! command -v psql &> /dev/null; then
    print_error "psql не найден! Убедитесь, что PostgreSQL установлен и psql доступен в PATH"
    exit 1
fi

# Проверка доступности сервера
if ! pg_isready -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" &> /dev/null; then
    print_error "Не удается подключиться к серверу PostgreSQL"
    print_error "Проверьте, что сервер запущен и параметры подключения корректны"
    exit 1
fi

print_success "Подключение к PostgreSQL успешно"

# Проверка существования базы данных
print_info "Проверка существования базы данных '$DB_NAME'..."

if ! psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -lqt | cut -d \| -f 1 | grep -qw "$DB_NAME"; then
    print_warning "База данных '$DB_NAME' не существует"
    read -p "Создать базу данных? (да/нет): " CREATE_DB
    
    if [[ "$CREATE_DB" =~ ^(да|yes|y|Да|ДА)$ ]]; then
        print_info "Создание базы данных '$DB_NAME'..."
        createdb -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" "$DB_NAME"
        
        if [ $? -eq 0 ]; then
            print_success "База данных '$DB_NAME' создана успешно"
        else
            print_error "Ошибка создания базы данных"
            exit 1
        fi
    else
        print_error "База данных не существует. Создайте её вручную или разрешите скрипту создать её"
        exit 1
    fi
else
    print_success "База данных '$DB_NAME' найдена"
fi

# Выполнение SQL скрипта
echo ""
print_info "Начало выполнения SQL скрипта..."
print_info "Это может занять несколько минут..."
echo ""

# Выполняем SQL файл
psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -f "$SQL_FILE"

if [ $? -eq 0 ]; then
    echo ""
    print_success "============================================================================="
    print_success "         ИНИЦИАЛИЗАЦИЯ БАЗЫ ДАННЫХ ЗАВЕРШЕНА УСПЕШНО!"
    print_success "============================================================================="
    echo ""
    print_success "База данных готова к работе с приложением Personal Finance Tools"
    echo ""
    print_info "Системные пользователи созданы:"
    print_info "  - admin@finance.com (роль: ADMIN)"
    print_info "  - manager@finance.com (роль: MANAGER)"
    print_info "  - Пароль по умолчанию: password"
    echo ""
    print_info "Справочники заполнены:"
    print_info "  - 6 валют (USD, EUR, BYN, RUB, PLN, UAH)"
    print_info "  - 9 категорий операций"
    echo ""
    print_warning "ВАЖНО: Смените пароли системных пользователей в продакшене!"
    echo ""
else
    echo ""
    print_error "============================================================================="
    print_error "              ОШИБКА ПРИ ВЫПОЛНЕНИИ SQL СКРИПТА!"
    print_error "============================================================================="
    print_error "Проверьте логи выше для определения причины ошибки"
    exit 1
fi