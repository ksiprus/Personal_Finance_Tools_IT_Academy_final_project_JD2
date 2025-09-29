@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

:: =============================================================================
:: СКРИПТ ИНИЦИАЛИЗАЦИИ БАЗЫ ДАННЫХ PERSONAL FINANCE TOOLS (WINDOWS)
:: =============================================================================
:: Описание: Автоматическая инициализация базы данных PostgreSQL для Windows
:: Автор: Personal Finance Tools Team
:: Версия: 1.0
:: =============================================================================

echo ============================================================================
echo          ИНИЦИАЛИЗАЦИЯ БАЗЫ ДАННЫХ PERSONAL FINANCE TOOLS
echo ============================================================================
echo.

:: Получение директории скрипта
set "SCRIPT_DIR=%~dp0"
set "SQL_FILE=%SCRIPT_DIR%complete_database_setup.sql"

echo [INFO] Текущая директория: %SCRIPT_DIR%
echo [INFO] SQL файл: %SQL_FILE%

:: Проверка существования SQL файла
if not exist "%SQL_FILE%" (
    echo [ERROR] Файл complete_database_setup.sql не найден!
    echo [ERROR] Ожидаемый путь: %SQL_FILE%
    pause
    exit /b 1
)

echo [SUCCESS] SQL файл найден

:: Параметры по умолчанию
set "DEFAULT_USER=postgres"
set "DEFAULT_HOST=localhost"
set "DEFAULT_PORT=5432"
set "DEFAULT_DATABASE=finance_tool"

:: Получение параметров подключения
echo.
echo [INFO] Настройка подключения к базе данных:
echo.

set /p "DB_USER=Пользователь PostgreSQL [%DEFAULT_USER%]: "
if "%DB_USER%"=="" set "DB_USER=%DEFAULT_USER%"

set /p "DB_HOST=Хост [%DEFAULT_HOST%]: "
if "%DB_HOST%"=="" set "DB_HOST=%DEFAULT_HOST%"

set /p "DB_PORT=Порт [%DEFAULT_PORT%]: "
if "%DB_PORT%"=="" set "DB_PORT=%DEFAULT_PORT%"

set /p "DB_NAME=Название базы данных [%DEFAULT_DATABASE%]: "
if "%DB_NAME%"=="" set "DB_NAME=%DEFAULT_DATABASE%"

echo.
echo [INFO] Параметры подключения:
echo [INFO]   Пользователь: %DB_USER%
echo [INFO]   Хост: %DB_HOST%
echo [INFO]   Порт: %DB_PORT%
echo [INFO]   База данных: %DB_NAME%
echo.

:: Предупреждение о безопасности
echo [WARNING] ВНИМАНИЕ!
echo [WARNING] Этот скрипт удалит все существующие данные в схемах:
echo [WARNING]   - finance_tool_shema
echo [WARNING]   - classifier
echo.
set /p "CONFIRM=Вы уверены, что хотите продолжить? (да/нет): "

if /i not "%CONFIRM%"=="да" if /i not "%CONFIRM%"=="yes" if /i not "%CONFIRM%"=="y" (
    echo [INFO] Операция отменена пользователем
    pause
    exit /b 0
)

:: Проверка доступности psql
where psql >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] psql не найден! Убедитесь, что PostgreSQL установлен и psql доступен в PATH
    pause
    exit /b 1
)

echo [SUCCESS] psql найден

:: Проверка подключения к PostgreSQL
echo [INFO] Проверка подключения к PostgreSQL...

pg_isready -h %DB_HOST% -p %DB_PORT% -U %DB_USER% >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Не удается подключиться к серверу PostgreSQL
    echo [ERROR] Проверьте, что сервер запущен и параметры подключения корректны
    pause
    exit /b 1
)

echo [SUCCESS] Подключение к PostgreSQL успешно

:: Проверка существования базы данных
echo [INFO] Проверка существования базы данных '%DB_NAME%'...

psql -h %DB_HOST% -p %DB_PORT% -U %DB_USER% -lqt | findstr /C:"%DB_NAME%" >nul
if %errorlevel% neq 0 (
    echo [WARNING] База данных '%DB_NAME%' не существует
    set /p "CREATE_DB=Создать базу данных? (да/нет): "
    
    if /i "!CREATE_DB!"=="да" (
        echo [INFO] Создание базы данных '%DB_NAME%'...
        createdb -h %DB_HOST% -p %DB_PORT% -U %DB_USER% %DB_NAME%
        
        if !errorlevel! equ 0 (
            echo [SUCCESS] База данных '%DB_NAME%' создана успешно
        ) else (
            echo [ERROR] Ошибка создания базы данных
            pause
            exit /b 1
        )
    ) else (
        echo [ERROR] База данных не существует. Создайте её вручную или разрешите скрипту создать её
        pause
        exit /b 1
    )
) else (
    echo [SUCCESS] База данных '%DB_NAME%' найдена
)

:: Выполнение SQL скрипта
echo.
echo [INFO] Начало выполнения SQL скрипта...
echo [INFO] Это может занять несколько минут...
echo.

:: Выполняем SQL файл
psql -h %DB_HOST% -p %DB_PORT% -U %DB_USER% -d %DB_NAME% -f "%SQL_FILE%"

if %errorlevel% equ 0 (
    echo.
    echo [SUCCESS] =============================================================================
    echo [SUCCESS]          ИНИЦИАЛИЗАЦИЯ БАЗЫ ДАННЫХ ЗАВЕРШЕНА УСПЕШНО!
    echo [SUCCESS] =============================================================================
    echo.
    echo [SUCCESS] База данных готова к работе с приложением Personal Finance Tools
    echo.
    echo [INFO] Системные пользователи созданы:
    echo [INFO]   - admin@finance.com ^(роль: ADMIN^)
    echo [INFO]   - manager@finance.com ^(роль: MANAGER^)
    echo [INFO]   - Пароль по умолчанию: password
    echo.
    echo [INFO] Справочники заполнены:
    echo [INFO]   - 6 валют ^(USD, EUR, BYN, RUB, PLN, UAH^)
    echo [INFO]   - 9 категорий операций
    echo.
    echo [WARNING] ВАЖНО: Смените пароли системных пользователей в продакшене!
    echo.
) else (
    echo.
    echo [ERROR] =============================================================================
    echo [ERROR]               ОШИБКА ПРИ ВЫПОЛНЕНИИ SQL СКРИПТА!
    echo [ERROR] =============================================================================
    echo [ERROR] Проверьте логи выше для определения причины ошибки
    pause
    exit /b 1
)

pause