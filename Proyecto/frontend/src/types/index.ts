// src/types/index.ts

export interface User {
  id: number;
  email: string;
  
}

export interface AuthResponse {
  token: string;
  user: User;
}

export type TaskPriority = 'alta' | 'media' | 'baja';

export const TranscriptionStatus = {
  PROCESSING: 'PROCESSING',
  TRANSCRIBING: 'TRANSCRIBING',
  GENERATING_TAGS: 'GENERATING_TAGS',
  GENERATING_EMBEDDINGS: 'GENERATING_EMBEDDINGS',
  COMPLETED: 'COMPLETED',
  ERROR: 'ERROR',
} as const; // <-- ¡La magia está aquí!

export type TTranscriptionStatus = typeof TranscriptionStatus[keyof typeof TranscriptionStatus];

export interface Task {
  id: number;
  description: string;
  priority: TaskPriority;
  dueDate: string; 
  completed: boolean;
}

export interface Transcription {
  id: number;
  title: string;
  status: TTranscriptionStatus; // <-- Usamos el nuevo tipo derivado
  createdAt: string;
  fullText?: string;
  summary?: string;
  tags?: string[];
  tasks?: Task[];
}
export interface Note {
  id: number;
  content: string;
  createdAt: string;
  updatedAt: string;
}

export interface ChatMessage {
  role: 'user' | 'assistant';
  content: string;
}

export interface ChatRequest {
  message: string;
  transcriptionId?: number;
}