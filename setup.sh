#!/bin/bash
# Script de configuración y ejecución inicial del proyecto ClassMate AI
# Automatiza: clonación, instalación de dependencias y configuración completa

echo "--- 🚀 Iniciando configuración de ClassMate AI ---"

# 1. Clonar repositorio
echo "[Paso 1/8] 📥 Clonando repositorio..."
git clone https://github.com/NISCALOOK/-No-Comments-.git
cd -- -No-Comments-

# 2. Configurar entorno virtual Python
echo "[Paso 2/8] 🐍 Configurando entorno virtual Python..."
python -m venv venv
source venv/bin/activate

# 3. Instalar Clientes NVIDIA (Python Clients)
echo "[Paso 3/8] 🔧 Instalando Clientes NVIDIA para transcripción..."
git clone https://github.com/NVIDIA-AI-IOT/python-clients.git
cd python-clients
pip install -r requirements.txt
cd ..

# 4. Iniciar base de datos PostgreSQL
echo "[Paso 4/8] 🐘 Iniciando base de datos PostgreSQL..."
docker compose up -d

# Esperar a que PostgreSQL esté listo
echo "⏳ Esperando a que PostgreSQL esté listo..."
sleep 10

# 5. Configurar Backend (Spring Boot)
echo "[Paso 5/8] ☕ Configurando Backend (Spring Boot)..."
cd Proyecto/backend

# Crear archivo de configuración si no existe
if [ ! -f "src/main/resources/application.properties" ]; then
    cp src/main/resources/application.properties.example src/main/resources/application.properties
    echo "⚠️  Por favor, edita src/main/resources/application.properties con tus API keys:"
    echo "   - llm.api.key (OpenAI o similar)"
    echo "   - whisper.api.key (OpenAI)"
    echo "   - nvidia.api.key (NVIDIA Embeddings)"
    echo "   - spring.datasource.password (PostgreSQL)"
fi

# Instalar dependencias y compilar
chmod +x mvnw
./mvnw clean install

# 6. Configurar Frontend (React)
echo "[Paso 6/8] ⚛️  Configurando Frontend (React)..."
cd ../frontend
npm install

# 7. Crear archivo .env si no existe
echo "[Paso 7/8] 🔐 Configurando variables de entorno..."
cd ../..
if [ ! -f ".env" ]; then
    cat > .env << EOF
OPENAI_API_KEY=tu_api_key_aqui
NVIDIA_API_KEY=tu_api_key_nvidia_aqui
DB_PASSWORD=tu_password_postgresql
EOF
    echo "⚠️  Por favor, edita el archivo .env con tus API keys reales"
fi

# 8. Iniciar servicios
echo "[Paso 8/8] 🚀 Iniciando servicios..."

echo "🌐 Iniciando Frontend (Terminal 1)..."
cd Proyecto/frontend
npm run dev &
FRONTEND_PID=$!

echo "☕ Iniciando Backend (Terminal 2)..."
cd ../backend
./mvnw spring-boot:run &
BACKEND_PID=$!

echo ""
echo "✅ ¡Configuración completa!"
echo ""
echo "🌐 Frontend: http://localhost:5173"
echo "🔌 Backend API: http://localhost:8080"
echo ""
echo "⚠️  No olvides:"
echo "   1. Configurar tus API keys en application.properties y .env"
echo "   2. Presiona Ctrl+C para detener los servicios"
echo ""

# Esperar a que los servicios inicien
wait $FRONTEND_PID $BACKEND_PID
