// src/components/Dashboard/TranscriptionViewer/TranscriptionViewer.tsx
import React, { useState, useEffect } from 'react';
import { getAllTranscriptions, getTranscriptionById } from '../../../api/transcriptions';
import type { Transcription } from '../../../types';
import './TranscriptionViewer.css'; // Asegúrate de que esta línea esté presente

const TranscriptionViewer: React.FC = () => {
  const [transcriptions, setTranscriptions] = useState<Transcription[]>([]);
  const [selectedTranscription, setSelectedTranscription] = useState<Transcription | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchTranscriptions = async () => {
      try {
        const data = await getAllTranscriptions();
        setTranscriptions(data);
        if (data.length > 0) {
          const details = await getTranscriptionById(data[0].id);
          setSelectedTranscription(details);
        }
      } catch (err: any) {
        setError(err.response?.data?.message || 'Error al cargar las transcripciones.');
      } finally {
        setIsLoading(false);
      }
    };
    fetchTranscriptions();
  }, []);

  const handleSelectTranscription = async (id: number) => {
    if (selectedTranscription?.id === id) return;
    try {
        const details = await getTranscriptionById(id);
        setSelectedTranscription(details);
    } catch (err: any) {
        setError('Error al cargar los detalles de la transcripción.');
    }
  };

  if (isLoading) {
    return <div className="loading-container"><p>Cargando lista de transcripciones...</p></div>;
  }
  if (error) {
    return <p className="error-message">{error}</p>;
  }

  return (
    // <-- CLASE PRINCIPAL DEL LAYOUT
    <div className="transcription-viewer-container">
      
      {/* <-- PANEL DE LA LISTA */}
      <div className="transcription-list">
        <h3 className="list-header">Transcripciones</h3>
        {transcriptions.length === 0 ? (
          <p className="empty-state-message">No tienes transcripciones aún. ¡Sube un audio!</p>
        ) : (
          <ul className="list-items">
            {transcriptions.map((t) => (
              <li
                key={t.id}
                onClick={() => handleSelectTranscription(t.id)}
                // <-- CLASE DINÁMICA PARA EL ITEM ACTIVO
                className={`transcription-item ${selectedTranscription?.id === t.id ? 'is-active' : ''}`}
              >
                <strong className="item-title">{t.title}</strong>
                <br />
                <small className="item-date">{new Date(t.createdAt).toLocaleDateString()}</small>
                <br />
                {/* <-- CLASE DINÁMICA PARA EL ESTADO */}
                <span className={`status-badge ${t.status === 'COMPLETED' ? 'status-completed' : 'status-pending'}`}>
                  {t.status}
                </span>
              </li>
            ))}
          </ul>
        )}
      </div>
      
      {/* <-- PANEL DE DETALLES */}
      <div className="transcription-details">
        {selectedTranscription ? (
          <div className="details-content">
            <h2 className="details-title">{selectedTranscription.title}</h2>
            <p className="details-info"><strong>Estado:</strong> {selectedTranscription.status}</p>
            <p className="details-info"><strong>Creada:</strong> {new Date(selectedTranscription.createdAt).toLocaleString()}</p>
            
            {selectedTranscription.fullText && (
              <section>
                <h3 className="details-section-title">Texto Completo</h3>
                {/* <-- CLASE PARA EL CONTENEDOR DE TEXTO */}
                <p className="content-box full-text-content">
                  {selectedTranscription.fullText}
                </p>
              </section>
            )}
            {selectedTranscription.summary && (
              <section>
                <h3 className="details-section-title">Resumen (IA)</h3>
                {/* <-- CLASE PARA EL CONTENEDOR DE RESUMEN */}
                <p className="content-box summary-content">
                  {selectedTranscription.summary}
                </p>
              </section>
            )}
            {selectedTranscription.tags && selectedTranscription.tags.length > 0 && (
              <section>
                <h3 className="details-section-title">Etiquetas</h3>
                <div className="tags-container">
                  {selectedTranscription.tags.map(tag => (
                    // <-- CLASE PARA CADA ETIQUETA -->
                    <span key={tag} className="tag">
                      #{tag}
                    </span>
                  ))}
                </div>
              </section>
            )}
            {selectedTranscription.tasks && selectedTranscription.tasks.length > 0 && (
              <section>
                <h3 className="details-section-title">Tareas Generadas</h3>
                <ul className="tasks-list">
                  {selectedTranscription.tasks.map(task => (
                    <li key={task.id} className="task-item">
                      {task.description} <span className="task-priority">(Prioridad: {task.priority})</span>
                    </li>
                  ))}
                </ul>
              </section>
            )}
          </div>
        ) : (
          <p className="placeholder-message">Selecciona una transcripción de la lista para ver sus detalles.</p>
        )}
      </div>
    </div>
  );
};

export default TranscriptionViewer;