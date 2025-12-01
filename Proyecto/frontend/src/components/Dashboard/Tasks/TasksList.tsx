// src/components/Dashboard/Tasks/TasksList.tsx

import React, { useState, useEffect } from 'react';
import type { Task } from '../../../types';
import { getAllTasks, updateTask, deleteTask } from '../../../api/task';


const TasksList: React.FC = () => {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchTasks = async () => {
      try {
        const data = await getAllTasks();
        setTasks(data);
      } catch (err: any) {
        setError(err.response?.data?.message || 'Error al cargar las tareas.');
      } finally {
        setLoading(false);
      }
    };

    fetchTasks();
  }, []);

  const handleToggleComplete = async (taskId: number) => {
    const taskToUpdate = tasks.find(t => t.id === taskId);
    if (!taskToUpdate) return;

    const originalState = taskToUpdate.completed;
    // Optimistic UI update
    setTasks(tasks.map(t => t.id === taskId ? { ...t, completed: !t.completed } : t));

    try {
      await updateTask(taskId, { isCompleted: !taskToUpdate.completed });
    } catch (err) {
      // Revert on error
      setTasks(tasks.map(t => t.id === taskId ? { ...t, completed: originalState } : t));
      setError('No se pudo actualizar la tarea.');
    }
  };

  const handleDelete = async (taskId: number) => {
    if (!window.confirm('¿Estás seguro de que quieres eliminar esta tarea?')) return;
    
    const originalTasks = tasks;
    setTasks(tasks.filter(t => t.id !== taskId));

    try {
      await deleteTask(taskId);
    } catch (err) {
      setTasks(originalTasks); // Revert on error
      setError('No se pudo eliminar la tarea.');
    }
  };

  if (loading) return <p>Cargando tareas...</p>;
  if (error) return <p style={{ color: 'red' }}>{error}</p>;

  return (
    <div>
      <h2>Mis Tareas</h2>
      {tasks.length === 0 ? (
        <p>No tienes tareas pendientes.</p>
      ) : (
        <ul>
          {tasks.map(task => (
            <li key={task.id} style={{ marginBottom: '10px', display: 'flex', alignItems: 'center', gap: '10px' }}>
              <input
                type="checkbox"
                checked={task.completed}
                onChange={() => handleToggleComplete(task.id)}
              />
              <span style={{ textDecoration: task.completed ? 'line-through' : 'none' }}>
                {task.description}
              </span>
              <span style={{ fontSize: '0.8em', color: 'gray' }}>
                (Prioridad: {task.priority}, Vence: {new Date(task.dueDate).toLocaleDateString()})
              </span>
              <button onClick={() => handleDelete(task.id)} style={{ color: 'red' }}>
                Eliminar
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default TasksList;