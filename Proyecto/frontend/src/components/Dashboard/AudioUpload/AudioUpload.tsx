// src/components/Dashboard/AudioUpload/AudioUpload.tsx
import React, { useState, useCallback, useRef } from 'react';
import { uploadAudio, getTranscriptionStatus } from '../../../api/transcriptions';
import { useAuthStore } from '../../../store';
import { TranscriptionStatus } from '../../../types'; // Valor
import type { Transcription} from '../../../types';

const AudioUpload: React.FC = () => {
  const { token } = useAuthStore();
  const [file, setFile] = useState<File | null>(null);
  const [isUploading, setIsUploading] = useState(false);
  const [currentTranscription, setCurrentTranscription] = useState<Transcription | null>(null);

  const [error, setError] = useState<string | null>(null);
  const pollingIntervalRef = useRef<ReturnType<typeof setInterval> | null>(null);

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files) {
      setFile(event.target.files[0]);
      setError(null);
      setCurrentTranscription(null);
    }
  };

  const pollStatus = useCallback(async (id: number) => {
    try {
      const status = await getTranscriptionStatus(id);
      setCurrentTranscription(status);
      // Ahora TypeScript sabe que `status.status` es de tipo `TranscriptionStatus`
      if (status.status === 'COMPLETED' || status.status === 'ERROR') {
        setIsUploading(false);
        if (pollingIntervalRef.current) {
          clearInterval(pollingIntervalRef.current);
        }
      }
    } catch (err) {
      setError('Error al verificar el estado del audio.');
      setIsUploading(false);
      if (pollingIntervalRef.current) {
        clearInterval(pollingIntervalRef.current);
      }
    }
  }, []);

  const handleUpload = async () => {
    if (!file || !token) return;

    setIsUploading(true);
    setError(null);
    try {
      const newTranscription = await uploadAudio(file);
      setCurrentTranscription(newTranscription);

      pollingIntervalRef.current = setInterval(() => {
        pollStatus(newTranscription.id);
      }, 3000);

    } catch (err: any) {
    // --- MEJORA CLAVE AQUÍ ---
    console.error("Error completo al subir archivo:", err); // <-- Añade esto para verlo en la consola del navegador

    let errorMessage = 'Error al subir el archivo.'; // Mensaje por defecto

    if (err.response) {
      // El servidor respondió con un código de error (4xx, 5xx)
      console.error("Datos del error del servidor:", err.response.data);
      // Intenta obtener el mensaje de error del cuerpo de la respuesta
      errorMessage = err.response.data?.message || JSON.stringify(err.response.data);
    } else if (err.request) {
      // La petición se hizo pero no hubo respuesta (problema de red)
      errorMessage = 'No se pudo conectar con el servidor. Revisa tu conexión.';
    } else {
      // Otro tipo de error (ej. error de configuración de axios)
      errorMessage = err.message;
    }
    
    setError(errorMessage);
    setIsUploading(false);
  }
  };

  React.useEffect(() => {
    return () => {
      if (pollingIntervalRef.current) {
        clearInterval(pollingIntervalRef.current);
      }
    };
  }, []);

  return (
    <div>
      <h2>Subir Audio para Transcribir</h2>
      <input type="file" accept="audio/*" onChange={handleFileChange} disabled={isUploading} />
      <button onClick={handleUpload} disabled={!file || isUploading}>
        {isUploading ? 'Procesando...' : 'Subir y Transcribir'}
      </button>

      {error && <p style={{ color: 'red' }}>{error}</p>}

      {currentTranscription && (
        <div>
          <h3>Estado de la Transcripción</h3>
          <p><strong>ID:</strong> {currentTranscription.id}</p>
          <p><strong>Título:</strong> {currentTranscription.title}</p>
          <p><strong>Estado:</strong> {currentTranscription.status}</p>
          
          {/* Gracias al tipado correcto, aquí TypeScript nos ayudará con autocompletado y validación */}
          {currentTranscription.status === TranscriptionStatus.COMPLETED && (
            <p>✅ ¡Transcripción completada! Puedes verla en la sección de <a href="/dashboard/transcription">Transcripciones</a>.</p>
          )}
          {currentTranscription.status === TranscriptionStatus.ERROR && (
             <p>❌ Ocurrió un error durante el procesamiento.</p>
          )}
        </div>
      )}
    </div>
  );
};

export default AudioUpload;