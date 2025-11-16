import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import AudioUpload from './AudioUpload';

describe('AudioUpload', () => { 
  it('debería renderizar el título y el botón de selección', () => {
    render(<AudioUpload onFileSelect={vi.fn()} isLoading={false} />);
    expect(screen.getByText('Aquí vas a subir tu audio')).toBeInTheDocument();
    expect(screen.getByText('Seleccionar Archivo de Audio')).toBeInTheDocument();
  });

  it('debería llamar a onFileSelect cuando se selecciona un archivo', () => {
    const mockOnFileSelect = vi.fn();
    render(<AudioUpload onFileSelect={mockOnFileSelect} isLoading={false} />);
    
    const fakeFile = new File(['audio'], 'lesson.mp3', { type: 'audio/mpeg' });
    const input = screen.getByTestId('audio-upload-input');
    
    fireEvent.change(input, { target: { files: [fakeFile] } });
    
    expect(mockOnFileSelect).toHaveBeenCalledTimes(1);
    expect(mockOnFileSelect).toHaveBeenCalledWith(fakeFile);
  });

  it('debería estar deshabilitado y mostrar "Subiendo..." cuando isLoading es true', () => {
    render(<AudioUpload onFileSelect={vi.fn()} isLoading={true} />);
    
    const input = screen.getByTestId('audio-upload-input');
    expect(input).toBeDisabled();
    expect(screen.getByText('Subiendo...')).toBeInTheDocument();
  });
});