@echo off
REM Script de configuración y ejecución inicial del proyecto ClassMate AI
REM Automatiza: clonación, instalación de dependencias y configuración completa

echo --- 🚀 Iniciando configuración de ClassMate AI ---

REM 1. Clonar repositorio
echo [Paso 1/8] 📥 Clonando repositorio...
git clone https://github.com/NISCALOOK/-No-Comments-.git
cd -No-Comments-

REM 2. Configurar entorno virtual Python
echo [Paso 2/8] 🐍 Configurando entorno virtual Python...
python -m venv venv
call venv\Scripts\activate.bat

REM 3. Instalar Clientes NVIDIA (Python Clients)
echo [Paso 3/8] 🔧 Instalando Clientes NVIDIA para transcripción...
git clone https://github.com/NVIDIA-AI-IOT/python-clients.git
cd python-clients
pip install -r requirements.txt
cd ..

REM 4. Iniciar base de datos PostgreSQL
echo [Paso 4/8] 🐘 Iniciando base de datos PostgreSQL...
docker compose up -d

REM Esperar a que PostgreSQL esté listo
echo ⏳ Esperando a que PostgreSQL esté listo...
timeout /t 10 /nobreak >nul

REM 5. Configurar Backend (Spring Boot)
echo [Paso 5/8] ☕ Configurando Backend (Spring Boot)...
cd Proyecto\backend

REM Crear archivo de configuración si no existe
if not exist "src\main\resources\application.properties" (
    copy "src\main\resources\application.properties.example" "src\main\resources\application.properties"
    echo ⚠️  Por favor, edita src\main\resources\application.properties con tus API keys:
    echo    - llm.api.key (OpenAI o similar)
    echo    - whisper.api.key (OpenAI)
    echo    - nvidia.api.key (NVIDIA Embeddings)
    echo    - spring.datasource.password (PostgreSQL)
)

REM Instalar dependencias y compilar
call mvnw clean install

REM 6. Configurar Frontend (React)
echo [Paso 6/8] ⚛️  Configurando Frontend (React)...
cd ..\frontend
call npm install

REM 7. Crear archivo .env si no existe
echo [Paso 7/8] 🔐 Configurando variables de entorno...
cd ..\..
if not exist ".env" (
    echo OPENAI_API_KEY=tu_api_key_aqui > .env
    echo NVIDIA_API_KEY=tu_api_key_nvidia_aqui >> .env
    echo DB_PASSWORD=tu_password_postgresql >> .env
    echo ⚠️  Por favor, edita el archivo .env con tus API keys reales
)

REM 8. Iniciar servicios
echo [Paso 8/8] 🚀 Iniciando servicios...

echo 🌐 Iniciando Frontend (Terminal 1)...
cd Proyecto\frontend
start "Frontend" cmd /k "npm run dev"

echo ☕ Iniciando Backend (Terminal 2)...
cd ..\backend
start "Backend" cmd /k "call mvnw spring-boot:run"

echo.
echo ✅ ¡Configuración completa!
echo.
echo 🌐 Frontend: http://localhost:5173
echo 🔌 Backend API: http://localhost:8080
echo.
echo ⚠️  No olvides:
echo    1. Configurar tus API keys en application.properties y .env
echo    2. Las nuevas terminales se abrirán automáticamente
echo.

pause
