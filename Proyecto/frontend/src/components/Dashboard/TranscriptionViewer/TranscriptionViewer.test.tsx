import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import TranscriptionViewer from './TranscriptionViewer';

describe('TranscriptionViewer', () => { 
  it('debería mostrar el mensaje de carga cuando isLoading es true', () => {
    render(
      <TranscriptionViewer 
        isLoading={true} 
        error={null} 
        summary={null} 
        notes={null} 
      />
    );
    expect(screen.getByText('Generando resumen...')).toBeInTheDocument();
  });

  it('debería mostrar el mensaje de error cuando la prop error tiene un valor', () => {
    render(
      <TranscriptionViewer 
        isLoading={false} 
        error="Error de conexión" 
        summary={null} 
        notes={null} 
      />
    );
    expect(screen.getByText('Error: Error de conexión')).toBeInTheDocument();
  });

  it('debería mostrar el resumen y las notas cuando se proporcionan', () => {
    render(
      <TranscriptionViewer 
        isLoading={false} 
        error={null} 
        summary="La clase trató sobre..." 
        notes="1. Punto importante." 
      />
    );
    expect(screen.getByText('Aquí vas a poder ver la transcripción')).toBeInTheDocument();
    expect(screen.getByText('Resumen')).toBeInTheDocument();
    expect(screen.getByText('La clase trató sobre...')).toBeInTheDocument();
  });
});