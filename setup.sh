#!/bin/bash
# Script de configuración y ejecución inicial del proyecto ClassMate AI
# Automatiza: clonación, instalación de dependencias y pruebas básicas

echo "--- Iniciando configuración de ClassMate AI ---"

# 1. Clonar repositorio
echo "[Paso 1/5] Clonando repositorio..."
git clone https://github.com/NISCALOOK/-No-Comments-.git
cd -- -No-Comments-

# 2. Iniciar base de datos (desde raíz del proyecto)
echo "[Paso 2/5] Iniciando base de datos PostgreSQL..."
docker compose up -d

cd Proyecto/holamundo
chmod +x mvnw

# [Paso 2] Instalar dependencias
echo "[Paso 2/3] Instalando dependencias del proyecto..."
./mvnw clean install

# [Paso 3] Ejecutar pruebas básicas
echo "[Paso 3/3] Ejecutando pruebas básicas..."
./mvnw test

# [Paso 4] Ejecución inicial
echo "--- Configuración completa. Iniciando el servidor ---"
echo "Presiona Ctrl+C para detener el servidor"
./mvnw spring-boot:run
