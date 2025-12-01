// src/components/Dashboard/TranscriptionViewer/TranscriptionViewer.tsx

import React, { useState, useEffect } from 'react';
import { getAllTranscriptions, getTranscriptionById } from '../../../api/transcriptions';
import type { Transcription } from '../../../types';

const TranscriptionViewer: React.FC = () => {
  // Estado para la lista de todas las transcripciones
  const [transcriptions, setTranscriptions] = useState<Transcription[]>([]);
  // Estado para la transcripción seleccionada que se mostrará al detalle
  const [selectedTranscription, setSelectedTranscription] = useState<Transcription | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Efecto para cargar la lista de transcripciones al montar el componente
  useEffect(() => {
    const fetchTranscriptions = async () => {
      try {
        const data = await getAllTranscriptions();
        setTranscriptions(data);
        // Opcional: Seleccionar la primera transcripción por defecto
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
  }, []); // El array vacío significa que se ejecuta solo una vez

  const handleSelectTranscription = async (id: number) => {
    // Para evitar recargas innecesarias si el mismo ya está seleccionado
    if (selectedTranscription?.id === id) return;

    try {
        // Podríamos añadir un estado de carga individual aquí si quisiéramos
        const details = await getTranscriptionById(id);
        setSelectedTranscription(details);
    } catch (err: any) {
        setError('Error al cargar los detalles de la transcripción.');
    }
  };

  if (isLoading) {
    return <p>Cargando lista de transcripciones...</p>;
  }

  if (error) {
    return <p style={{ color: 'red' }}>{error}</p>;
  }

  return (
    <div style={{ display: 'flex', height: '100%' }}>
      {/* Lista de Transcripciones */}
      <div style={{ width: '30%', borderRight: '1px solid #ccc', padding: '1rem' }}>
        <h3>Transcripciones</h3>
        {transcriptions.length === 0 ? (
          <p>No tienes transcripciones aún. ¡Sube un audio!</p>
        ) : (
          <ul style={{ listStyle: 'none', padding: 0 }}>
            {transcriptions.map((t) => (
              <li
                key={t.id}
                onClick={() => handleSelectTranscription(t.id)}
                style={{
                  padding: '10px',
                  cursor: 'pointer',
                  backgroundColor: selectedTranscription?.id === t.id ? '#e0e0e0' : 'transparent',
                  borderRadius: '5px',
                  marginBottom: '5px'
                }}
              >
                <strong>{t.title}</strong>
                <br />
                <small>{new Date(t.createdAt).toLocaleDateString()}</small>
                <br />
                <span style={{ color: t.status === 'COMPLETED' ? 'green' : 'orange' }}>
                  {t.status}
                </span>
              </li>
            ))}
          </ul>
        )}
      </div>

      {/* Vista de Detalles */}
      <div style={{ flexGrow: 1, padding: '1rem' }}>
        {selectedTranscription ? (
          <div>
            <h2>{selectedTranscription.title}</h2>
            <p><strong>Estado:</strong> {selectedTranscription.status}</p>
            <p><strong>Creada:</strong> {new Date(selectedTranscription.createdAt).toLocaleString()}</p>

            {selectedTranscription.fullText && (
              <section>
                <h3>Texto Completo</h3>
                <p style={{ whiteSpace: 'pre-wrap', background: '#e20a0aff', padding: '1rem', borderRadius: '5px' }}>
                  {selectedTranscription.fullText}
                </p>
              </section>
            )}

            {selectedTranscription.summary && (
              <section>
                <h3>Resumen (IA)</h3>
                <p style={{ background: '#f53615ff', padding: '1rem', borderRadius: '5px', borderLeft: '4px solid #1890ff' }}>
                  {selectedTranscription.summary}
                </p>
              </section>
            )}

            {selectedTranscription.tags && selectedTranscription.tags.length > 0 && (
              <section>
                <h3>Etiquetas</h3>
                <div>
                  {selectedTranscription.tags.map(tag => (
                    <span key={tag} style={{ background: '#cf1717ff', padding: '4px 8px', borderRadius: '12px', marginRight: '5px' }}>
                      #{tag}
                    </span>
                  ))}
                </div>
              </section>
            )}

            {selectedTranscription.tasks && selectedTranscription.tasks.length > 0 && (
              <section>
                <h3>Tareas Generadas</h3>
                <ul>
                  {selectedTranscription.tasks.map(task => (
                    <li key={task.id}>
                      {task.description} (Prioridad: {task.priority})
                    </li>
                  ))}
                </ul>
              </section>
            )}
          </div>
        ) : (
          <p>Selecciona una transcripción de la lista para ver sus detalles.</p>
        )}
      </div>
    </div>
  );
};

export default TranscriptionViewer;