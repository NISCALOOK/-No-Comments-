// src/api/auth.ts

import api from './api';
import type { AuthResponse } from '../types';

// Asumimos que el endpoint de login devuelve { token: string, user: User }
export const login = async (email: string, password: string): Promise<AuthResponse> => {
  const response = await api.post<AuthResponse>('/auth/login', { email, password });
  return response.data;
};

export const register = async (email: string, password: string): Promise<AuthResponse> => {
  const response = await api.post<AuthResponse>('/auth/register', { email, password });
  return response.data;
};