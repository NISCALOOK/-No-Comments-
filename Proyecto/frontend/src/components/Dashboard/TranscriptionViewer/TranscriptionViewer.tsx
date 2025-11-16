// src/components/Dashboard/TranscriptionViewer/TranscriptionViewer.tsx
import React from 'react';

interface TranscriptionViewerProps {
  isLoading: boolean;
  error: string | null;
  summary: string | null;
  notes: string | null;
}

// QUITAMOS "export" de aquí
const TranscriptionViewer: React.FC<TranscriptionViewerProps> = ({
  isLoading,
  error,
  summary,
  notes,
}) => {
  if (isLoading) {
    return <p>Generando resumen...</p>;
  }

  if (error) {
    return <p style={{ color: 'red' }}>Error: {error}</p>;
  }

  if (summary && notes) {
    return (
      <div className="audio-upload">
        <h2>Aquí vas a poder ver la transcripción</h2>
        <h3>Resumen</h3>
        <p>{summary}</p>
        <h3>Notas</h3>
        <p>{notes}</p>
      </div>
    );
  }

  return <p>Por favor, sube un audio para comenzar.</p>;
};

// AÑADIMOS "export default" AQUÍ
export default TranscriptionViewer;