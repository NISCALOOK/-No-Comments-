// src/api/transcriptions.ts

import api from './api';
import type { Transcription } from '../types';

export const uploadAudio = async (file: File): Promise<Transcription> => {
  const formData = new FormData();
  formData.append('file', file);

  const response = await api.post<Transcription>('/audio/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
  return response.data;
};

export const getTranscriptionStatus = async (id: number): Promise<Transcription> => {
  const response = await api.get<Transcription>(`/audio/status/${id}`);
  return response.data;
};

export const getAllTranscriptions = async (): Promise<Transcription[]> => {
  const response = await api.get<Transcription[]>('/transcriptions');
  return response.data;
};

export const getTranscriptionById = async (id: number): Promise<Transcription> => {
  const response = await api.get<Transcription>(`/transcriptions/${id}`);
  return response.data;
};