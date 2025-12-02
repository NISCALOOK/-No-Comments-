// src/api/chat.ts

import axios from 'axios'; 
import type { AxiosInstance, AxiosResponse } from 'axios';

const api: AxiosInstance = axios.create({
  // En Vite
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
  },
});


api.interceptors.request.use(
  (config) => {
    
    const token = localStorage.getItem('authToken');

    if (token) {
 
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
   
    return Promise.reject(error);
  }
);


export interface ChatRequest {
  message: string;
  transcriptionId?: number;
}

export interface ChatResponse {
   message: string;
}

export const sendMessage = async (payload: ChatRequest): Promise<ChatResponse> => {
  try {
 
    const response: AxiosResponse<ChatResponse> = await api.post('/api/chat', payload);
    
    
    return response.data;

  } catch (error) {
    
    console.error("Error calling sendMessage API:", error);
    throw error;
  }
};