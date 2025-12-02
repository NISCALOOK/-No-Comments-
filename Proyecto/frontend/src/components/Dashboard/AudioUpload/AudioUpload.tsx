// src/components/Dashboard/AudioUpload/AudioUpload.tsx
import React, { useState, useCallback, useRef } from 'react';
import { uploadAudio, getTranscriptionStatus } from '../../../api/transcriptions';
import { useAuthStore } from '../../../store';
import { TranscriptionStatus } from '../../../types';
import type { Transcription} from '../../../types';
import './AudioUpload.css'; // Asegúrate de que esta línea está presente

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
      console.error("Error completo al subir archivo:", err);
      let errorMessage = 'Error al subir el archivo.';
      if (err.response) {
        console.error("Datos del error del servidor:", err.response.data);
        errorMessage = err.response.data?.message || JSON.stringify(err.response.data);
      } else if (err.request) {
        errorMessage = 'No se pudo conectar con el servidor. Revisa tu conexión.';
      } else {
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
    // <-- CLASE PRINCIPAL AÑADIDA
    <div className="audio-upload-container"> 
      <h2 className="audio-upload-header">Subir Audio para Transcribir</h2>
      
      {/* <-- SECCIÓN DE CONTROLES AÑADIDA */}
      <div className="upload-controls">
        <input 
          type="file" 
          accept="audio/*" 
          onChange={handleFileChange} 
          disabled={isUploading}
          className="file-input" 
        />
        <button 
          onClick={handleUpload} 
          disabled={!file || isUploading}
          className="upload-button"
        >
          {isUploading ? 'Procesando...' : 'Subir y Transcribir'}
        </button>
      </div>

      {/* <-- CLASE DE ERROR AÑADIDA (reemplaza el estilo en línea) */}
      {error && <p className="error-message">{error}</p>}

      {currentTranscription && (
        // <-- SECCIÓN DE ESTADO AÑADIDA
        <div className="transcription-status">
          <h3 className="status-title">Estado de la Transcripción</h3>
          <p className="status-info"><strong>ID:</strong> {currentTranscription.id}</p>
          <p className="status-info"><strong>Título:</strong> {currentTranscription.title}</p>
          <p className="status-info"><strong>Estado:</strong> {currentTranscription.status}</p>
          
          {currentTranscription.status === TranscriptionStatus.COMPLETED && (
            // <-- CLASE DE ÉXITO AÑADIDA
            <p className="status-completed">✅ ¡Transcripción completada! Puedes verla en la sección de <a href="/dashboard/transcription">Transcripciones</a>.</p>
          )}
          {currentTranscription.status === TranscriptionStatus.ERROR && (
            // <-- CLASE DE ERROR DE ESTADO AÑADIDA
            <p className="status-error">❌ Ocurrió un error durante el procesamiento.</p>
          )}
        </div>
      )}
    </div>
  );
};

export default AudioUpload;