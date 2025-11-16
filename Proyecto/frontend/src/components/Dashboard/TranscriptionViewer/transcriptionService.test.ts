import { describe, it, expect, vi, beforeEach } from 'vitest';
import { startTranscription } from './transcriptionService';

const mockFetch = vi.fn();
global.fetch = mockFetch;

describe('transcriptionService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('debería devolver el taskId cuando la API responde correctamente', async () => {
    const fakeFile = new File(['audio'], 'test.mp3', { type: 'audio/mpeg' });
    const fakeApiResponse = { taskId: 'task-123-abc' };
    
    mockFetch.mockResolvedValueOnce({
      ok: true,
      json: async () => fakeApiResponse,
    });
    
    const result = await startTranscription(fakeFile);
    
    expect(result).toEqual(fakeApiResponse);
    expect(mockFetch).toHaveBeenCalledWith('/api/transcribe', {
      method: 'POST',
      body: expect.any(FormData),
    });
  });

  it('debería lanzar un error cuando la API responde con un estado de error', async () => {
    const fakeFile = new File(['audio'], 'test.mp3', { type: 'audio/mpeg' });
    
    mockFetch.mockResolvedValueOnce({ ok: false });
    
    await expect(startTranscription(fakeFile)).rejects.toThrow('Failed to start transcription');
  });
});