@echo off
REM Script de configuracion y ejecucion inicial del proyecto ClassMate AI
REM Automatiza: clonacion, instalacion de dependencias y pruebas basicas

echo --- Iniciando configuracion de ClassMate AI ---

REM 1. Clonar repositorio
echo [Paso 1/5] Clonando repositorio...
git clone https://github.com/NISCALOOK/-No-Comments-.git
cd -No-Comments-

REM 2. Iniciar base de datos (desde raiz del proyecto)
echo [Paso 2/5] Iniciando base de datos PostgreSQL...
docker compose up -d
cd Proyecto\holamundo

REM 3. Instalar dependencias
echo [Paso 3/5] Instalando dependencias del proyecto...
call mvnw clean install

REM 4. Ejecutar pruebas basicas
echo [Paso 4/5] Ejecutando pruebas basicas...
call mvnw test

REM 5. Ejecucion inicial
echo [Paso 5/5] --- Configuracion completa. Iniciando el servidor ---
echo Presiona Ctrl+C para detener el servidor
call mvnw spring-boot:run
