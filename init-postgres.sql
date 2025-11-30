-- Script de inicialización para PostgreSQL con pgvector
-- Se ejecuta automáticamente cuando el contenedor se inicia por primera vez

CREATE EXTENSION IF NOT EXISTS vector;
DO $$
BEGIN
    RAISE NOTICE 'Extensión pgvector instalada correctamente en la base de datos %', current_database();
END $$;
