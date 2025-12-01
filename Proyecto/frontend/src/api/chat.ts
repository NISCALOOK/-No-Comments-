// src/api/chat.ts

import api from './api';
import type { ChatRequest } from '../types';

export const sendMessage = async (payload: ChatRequest): Promise<{ response: string }> => {
  const response = await api.post<{ response: string }>('/chat', payload);
  return response.data;
};