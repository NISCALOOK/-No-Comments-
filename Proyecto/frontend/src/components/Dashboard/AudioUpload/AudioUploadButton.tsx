import React from 'react';

interface AudioUploadButtonProps {
  onFileSelect: (file: File) => void;
  isLoading: boolean;
}

export const AudioUploadButton: React.FC<AudioUploadButtonProps> = ({ onFileSelect, isLoading }) => {
  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files && event.target.files.length > 0) {
      onFileSelect(event.target.files[0]);
    }
  };

  return (
    <div className="audio-upload">
      <h2>Aquí vas a subir tu audio</h2>
      <label htmlFor="audio-upload" style={{ cursor: 'pointer', background: '#007bff', color: 'white', padding: '10px', borderRadius: '5px' }}>
        Seleccionar Archivo de Audio
      </label>
      <input 
        id="audio-upload"
        type="file" 
        accept="audio/*" 
        onChange={handleFileChange} 
        disabled={isLoading} 
        style={{ display: 'none' }} // Ocultamos el input feo y usamos el label como botón
        data-testid="audio-upload-input" 
      />
      {isLoading && <p style={{ marginTop: '10px' }}>Subiendo...</p>}
    </div>
  );
};