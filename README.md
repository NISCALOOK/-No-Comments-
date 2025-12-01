# ClassMate AI: Tu Asistente de Clases 🎓
<div id="header" align="left">
  <img src="ClassMateAI.png" width="600"/>
</div>

---

## 🚀 Impulsa tus clases con ClassMate AI

¿Te imaginas transformar las largas grabaciones de tus clases en apuntes inteligentes y organizados con un solo clic? ¡Bienvenido a **ClassMate AI**!

Nuestro proyecto es un asistente académico diseñado para revolucionar tu forma de estudiar. Olvídate de pasar horas transcribiendo y organizando; nuestra aplicación lo hace por ti. Simplemente sube el audio de tu clase y deja que nuestra inteligencia artificial genere:

-   **Transcripciones precisas** del audio.
-   **Resúmenes automáticos** con los puntos clave.
-   **Preguntas y respuestas** para facilitar el repaso.
-   **Listas de tareas y pendientes** extraídas directamente de la clase.

Pero eso no es todo. **ClassMate AI** va un paso más allá, permitiéndote:

-   **Sincronizar tareas** directamente con tu **Google Calendar**.
-   **Interactuar con un chat de IA** para resolver dudas específicas sobre el contenido.
-   **Personalizar y editar** los apuntes generados.
-   **Exportar** tus notas en formatos como PDF, Word y TXT.

Con una interfaz intuitiva, adaptable a cualquier dispositivo y disponible en varios idiomas, **ClassMate AI** es la herramienta definitiva para optimizar tu tiempo, mejorar tu rendimiento académico y mantener tu vida universitaria perfectamente organizada.

**¡Únete a nosotros y lleva tu aprendizaje al siguiente nivel!**

---

## 🛠️ Instalación y Configuración

### **Prerrequisitos**
- Java 17 o superior
- Node.js 18+
- Python 3.8+
- Docker y Docker Compose

### **1. Clonar el Repositorio**
```bash
git clone https://github.com/NISCALOOK/-No-Comments-.git
cd -No-Comments-
```

### **2. Configurar Entorno Virtual Python**
```bash
# Crear entorno virtual
python -m venv venv

# Activar entorno virtual
# Linux/Mac:
source venv/bin/activate
# Windows:
venv\Scripts\activate

### **3. Instalar Clientes NVIDIA (Python Clients)**
```bash
# Clonar clientes NVIDIA para transcripción
git clone https://github.com/nvidia-riva/python-clients.git

# Instalar dependencias de los clientes
cd python-clients
pip install -r requirements.txt
cd ..
```

### **4. Configurar Base de Datos**
```bash
# Iniciar PostgreSQL con Docker
docker-compose up -d

# Esperar a que la base de datos esté lista
docker-compose logs postgres
```

### **5. Configurar Backend (Spring Boot)**
```bash
cd Proyecto/backend

# Configurar application.properties
cp src/main/resources/application.properties.example src/main/resources/application.properties

# Editar application.properties con tus API keys:
# - llm.api.key (OpenAI o similar)
# - whisper.api.key (OpenAI)
# - nvidia.api.key (NVIDIA Embeddings)
# - spring.datasource.password (PostgreSQL)

# Compilar y ejecutar
./mvnw clean install
./mvnw spring-boot:run
```

### **6. Configurar Frontend (React)**
```bash
cd Proyecto/frontend

# Instalar dependencias
npm install

# Iniciar servidor de desarrollo
npm run dev
```

### **7. Variables de Entorno**
Crear archivo `.env` en la raíz del proyecto:
```env
OPENAI_API_KEY=tu_api_key_aqui
NVIDIA_API_KEY=tu_api_key_nvidia_aqui
DB_PASSWORD=tu_password_postgresql
```

---

## 📁 Estructura del Proyecto

```
-No-Comments-/
├── 📁 Proyecto/
│   ├── 📁 backend/          # Spring Boot (Java)
│   └── 📁 frontend/         # React (TypeScript)
├── 📁 python-clients/       # Clientes NVIDIA para transcripción
├── 📁 venv/                # Entorno virtual Python
├── 📁 Documentación/        # Documentación del proyecto
├── 📄 docker-compose.yml    # Configuración PostgreSQL
└── 📄 README.md            # Este archivo
```

---

## 🚀 Ejecución Rápida

Una vez configurado todo:

```bash
# 1. Iniciar base de datos
docker-compose up -d

# 2. Iniciar backend (Terminal 1)
cd Proyecto/backend
./mvnw spring-boot:run

# 3. Iniciar frontend (Terminal 2)
cd Proyecto/frontend
npm run dev

# 4. Acceder a la aplicación
# Frontend: http://localhost:5173
# Backend API: http://localhost:8080
```

---

## 📝 Notas Importantes

### **API Keys Requeridas**
- 🔑 **OpenAI API**: Para transcripción (Whisper) y LLM
- 🔑 **NVIDIA API**: Para embeddings del sistema RAG
- 🔑 **PostgreSQL**: Para base de datos local


---

## 👥 Integrantes del Grupo

Este proyecto es desarrollado por el grupo **# -No-Comments-** para la asignatura de Ingeniería de Software I de la Universidad Nacional de Colombia.

| Nombre Completo | Contacto (Email) |
| :-------------------------- | :------------------------------ |
| Nicolás Rodríguez Tapia | `nrodriguezt@unal.edu.co` |
| Juan David Alarcón Sanabria | `jalarconsa@unal.edu.co` |
| José Leonardo Pinilla Zamora | `jpinillaz@unal.edu.co` |
| David Nicolás Urrego Botero | `durregob@unal.edu.co` |
