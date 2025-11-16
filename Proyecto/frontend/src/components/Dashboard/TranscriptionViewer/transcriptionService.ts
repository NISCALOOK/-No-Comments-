export interface TranscriptionStartResponse {
  taskId: string;
}

export const startTranscription = async (audioFile: File): Promise<TranscriptionStartResponse> => {
  const formData = new FormData();
  formData.append('audio', audioFile);

  const response = await fetch('/api/transcribe', {
    method: 'POST',
    body: formData,
  });

  if (!response.ok) {
    throw new Error('Failed to start transcription');
  }

  return response.json();
};