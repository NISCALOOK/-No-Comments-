// src/api/tasks.ts

import api from './api';
import type { Task } from '../types';

export const getAllTasks = async (): Promise<Task[]> => {
  const response = await api.get<Task[]>('/tasks');
  return response.data;
};

// El backend espera un Partial<Task>, pero para simplificar, podemos crear una interfaz específica
interface UpdateTaskPayload {
  priority?: 'alta' | 'media' | 'baja';
  dueDate?: string; // ISO 8601
  isCompleted?: boolean;
}

export const updateTask = async (id: number, payload: UpdateTaskPayload): Promise<Task> => {
  const response = await api.put<Task>(`/tasks/${id}`, payload);
  return response.data;
};

export const deleteTask = async (id: number): Promise<void> => {
  await api.delete(`/tasks/${id}`);
};