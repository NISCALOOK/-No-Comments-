# 📡 Endpoints API - ClassMate AI Backend

## 🔐 Autenticación

### 1. Registrar Usuario
**POST** `/api/auth/register`

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Usuario Test",
    "email": "test@example.com",
    "password": "password123"
  }'
```

### 2. Iniciar Sesión
**POST** `/api/auth/login`

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

**Respuesta esperada:**
```json
{"token":"eyJhbGciOiJIUzI1NiJ9...","user":{"name":"Usuario Test","email":"test@example.com","id":9}}
```

---

## 🎙️ Transcripciones

### 1. Subir Audio
**POST** `/api/audio/upload`

```bash
curl -X POST http://localhost:8080/api/audio/upload \
  -H "Authorization: Bearer TU_TOKEN_AQUI" \
  -F "file=@audio.mp3"
```

**Respuesta esperada:**
```json
{"id":1,"title":"Procesando audio...","status":"PROCESSING","createdAt":"2025-11-28T15:58:25.986756061"}
```

### 2. Ver Estado de Transcripción
**GET** `/api/audio/status/{id}`

```bash
curl -X GET http://localhost:8080/api/audio/status/1 \
  -H "Authorization: Bearer TU_TOKEN_AQUI"
```

**Respuesta esperada:**
```json
{"id":1,"title":"Título generado","status":"COMPLETED","createdAt":"2025-11-28T15:58:25.986756"}
```

### 3. Listar Todas las Transcripciones
**GET** `/api/transcriptions`

```bash
curl -X GET http://localhost:8080/api/transcriptions \
  -H "Authorization: Bearer TU_TOKEN_AQUI"
```

**Respuesta esperada:**
```json
[
  {"id":1,"title":"Título 1","status":"COMPLETED","createdAt":"2025-11-28T15:58:25.986756"},
  {"id":2,"title":"Título 2","status":"PROCESSING","createdAt":"2025-11-28T15:59:30.123456"}
]
```

### 4. Obtener Detalles de Transcripción
**GET** `/api/transcriptions/{id}`

```bash
curl -X GET http://localhost:8080/api/transcriptions/1 \
  -H "Authorization: Bearer TU_TOKEN_AQUI"
```

**Respuesta esperada:**
```json
{
  "id":1,
  "title":"Título de la transcripción",
  "status":"COMPLETED",
  "createdAt":"2025-11-28T15:58:25.986756",
  "fullText":"Texto completo de la transcripción...",
  "summary":"Resumen generado por la IA...",
  "tags":["etiqueta1","etiqueta2","etiqueta3"],
  "tasks":[
    {"id":1,"description":"Tarea 1","priority":"alta","dueDate":"2025-12-03T19:00:00","completed":false}
  ]
}
```

---

## ✅ Tareas (Tasks)

### 1. Listar Todas las Tareas
**GET** `/api/tasks`

```bash
curl -X GET http://localhost:8080/api/tasks \
  -H "Authorization: Bearer TU_TOKEN_AQUI"
```

**Respuesta esperada:**
```json
[
  {"id":1,"description":"Parcial de probabilidad","priority":"alta","dueDate":"2025-12-03T19:00:00","completed":false},
  {"id":2,"description":"Examen de cálculo","priority":"alta","dueDate":"2025-12-08T18:00:00","completed":false}
]
```

### 2. Actualizar Tarea
**PUT** `/api/tasks/{id}`

```bash
curl -X PUT http://localhost:8080/api/tasks/1 \
  -H "Authorization: Bearer TU_TOKEN_AQUI" \
  -H "Content-Type: application/json" \
  -d '{
    "priority": "media",
    "dueDate": "2025-12-05T10:00:00",
    "isCompleted": false
  }'
```

**Respuesta esperada:**
```json
{"id":1,"description":"Parcial de probabilidad","priority":"media","dueDate":"2025-12-05T10:00:00","completed":false}
```

### 3. Marcar Tarea como Completada
**PUT** `/api/tasks/{id}` (solo con isCompleted)

```bash
curl -X PUT http://localhost:8080/api/tasks/1 \
  -H "Authorization: Bearer TU_TOKEN_AQUI" \
  -H "Content-Type: application/json" \
  -d '{
    "isCompleted": true
  }'
```

### 4. Eliminar Tarea
**DELETE** `/api/tasks/{id}`

```bash
curl -X DELETE http://localhost:8080/api/tasks/1 \
  -H "Authorization: Bearer TU_TOKEN_AQUI"
```

**Respuesta esperada:** `204 No Content` (éxito sin contenido)

---

## 🤖 Chat con IA

### 1. Chatear con Contexto General
**POST** `/api/chat`

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Authorization: Bearer TU_TOKEN_AQUI" \
  -H "Content-Type: application/json" \
  -d '{
    "message": "¿Cuáles son los conceptos principales de mis clases?"
  }'
```

### 2. Chatear con Contexto de Transcripción Específica
**POST** `/api/chat`

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Authorization: Bearer TU_TOKEN_AQUI" \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Explícame mejor el método de Newton",
    "transcriptionId": 1
  }'
```

**Respuesta esperada:**
```json
{"response":"El método de Newton es un algoritmo iterativo para encontrar las raíces de funciones..."}
```

---

## 📝 Notas

### 1. Listar Notas del Usuario
**GET** `/api/notes`

```bash
curl -X GET http://localhost:8080/api/notes \
  -H "Authorization: Bearer TU_TOKEN_AQUI"
```

**Respuesta esperada:**
```json
[
  {"id":1,"content":"Recordatorio: estudiar para el parcial","createdAt":"2025-11-28T15:58:25.986756","updatedAt":"2025-11-28T15:58:25.986756"},
  {"id":2,"content":"Ideas para proyecto final","createdAt":"2025-11-28T16:30:15.123456","updatedAt":"2025-11-28T16:30:15.123456"}
]
```

### 2. Crear Nota
**POST** `/api/notes`

```bash
curl -X POST http://localhost:8080/api/notes \
  -H "Authorization: Bearer TU_TOKEN_AQUI" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Esta es una nueva nota personal"
  }'
```

**Respuesta esperada:**
```json
{"id":3,"content":"Esta es una nueva nota personal","createdAt":"2025-11-28T17:00:00.000000","updatedAt":"2025-11-28T17:00:00.000000"}
```

### 3. Actualizar Nota
**PUT** `/api/notes/{id}`

```bash
curl -X PUT http://localhost:8080/api/notes/1 \
  -H "Authorization: Bearer TU_TOKEN_AQUI" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Nota actualizada con nueva información"
  }'
```

### 4. Eliminar Nota
**DELETE** `/api/notes/{id}`

```bash
curl -X DELETE http://localhost:8080/api/notes/1 \
  -H "Authorization: Bearer TU_TOKEN_AQUI"
```

---

## 🔧 Campos y Formatos

### TaskPriority (Enum)
- `alta`
- `media` 
- `baja`

### Estados de Transcripción
- `PROCESSING` - Procesando audio
- `TRANSCRIBING` - Transcribiendo con Whisper
- `GENERATING_TAGS` - Generando etiquetas
- `GENERATING_EMBEDDINGS` - Creando embeddings RAG
- `COMPLETED` - Proceso completado
- `ERROR` - Error en el procesamiento

### Formatos de Fecha/Hora
- **DueDate**: `YYYY-MM-DDTHH:MM:SS` (ej: `2025-12-03T19:00:00`)
- **CreatedAt/UpdatedAt**: `YYYY-MM-DDTHH:MM:SS.sss`

---

## 🚀 Flujo Completo de Uso

1. **Registrar usuario** → `/api/auth/register`
2. **Iniciar sesión** → `/api/auth/login` (obtener token)
3. **Subir audio** → `/api/audio/upload`
4. **Verificar estado** → `/api/audio/status/{id}`
5. **Obtener detalles** → `/api/transcriptions/{id}`
6. **Ver tareas generadas** → `/api/tasks`
7. **Chatear con IA** → `/api/chat`

---

## 📋 Notas Importantes

- **Token JWT**: Reemplaza `TU_TOKEN_AQUI` con el token obtenido en `/api/auth/login`
- **Seguridad**: Todos los endpoints (excepto `/api/auth/register` y `/api/auth/login`) requieren token
- **Archivos**: Los audios se procesan asíncronamente
- **Tareas automáticas**: Se generan automáticamente al procesar transcripciones
- **RAG**: El chat utiliza búsqueda vectorial para respuestas contextuales