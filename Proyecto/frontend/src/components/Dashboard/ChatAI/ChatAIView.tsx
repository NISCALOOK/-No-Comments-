// src/components/Dashboard/ChatAI/ChatAIView.tsx

import React, { useState, useRef, useEffect } from 'react';
import { sendMessage } from '../../../api/chat';
import { getAllTranscriptions } from '../../../api/transcriptions';
import type { ChatMessage, Transcription } from '../../../types';

const ChatAIView: React.FC = () => {
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [input, setInput] = useState('');
  const [selectedTranscriptionId, setSelectedTranscriptionId] = useState<number | undefined>();
  const [transcriptions, setTranscriptions] = useState<Transcription[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const fetchTranscriptions = async () => {
      try {
        const data = await getAllTranscriptions();
        setTranscriptions(data.filter(t => t.status === 'COMPLETED'));
      } catch (error) {
        console.error("Error al cargar transcripciones para el chat:", error);
      }
    };
    fetchTranscriptions();
  }, []);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);
   const cleanAiResponse = (rawResponse: string): string => {
   const cleanedResponse = rawResponse.replace(/<think>.*?<\/think>/gs, '').trim();
   return cleanedResponse;
    };


  const handleSend = async () => {
    if (!input.trim() || isLoading) return;

    const userMessage: ChatMessage = { role: 'user', content: input };
    setMessages(prev => [...prev, userMessage]);
    setInput('');
    setIsLoading(true);

    try {
      const { message } = await sendMessage({
        message: input,
        transcriptionId: selectedTranscriptionId,
      });
      
     const cleanedMessage = cleanAiResponse(message);
     
      const assistantMessage: ChatMessage = { role: 'assistant', content: cleanedMessage };
      setMessages(prev => [...prev, assistantMessage]);
    } catch (error: any) {
      const errorMessage: ChatMessage = { role: 'assistant', content: `Error: ${error.message?.data?.message || 'No se pudo conectar con el asistente.'}` };
      setMessages(prev => [...prev, errorMessage]);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '80vh' }}>
      <h2>Chat con Asistente IA</h2>
      
      <div style={{ marginBottom: '1rem' }}>
        <label>Contexto (Opcional): </label>
        <select value={selectedTranscriptionId || ''} onChange={(e) => setSelectedTranscriptionId(e.target.value ? Number(e.target.value) : undefined)}>
          <option value="">Ninguno (Chat General)</option>
          {transcriptions.map(t => (
            <option key={t.id} value={t.id}>{t.title}</option>
          ))}
        </select>
      </div>

      <div style={{ flexGrow: 1, overflowY: 'auto', border: '1px solid #ccc', padding: '1rem' }}>
        {messages.length === 0 && <p>¡Hola! ¿En qué puedo ayudarte hoy?</p>}
        {messages.map((msg, index) => (
          <div key={index} style={{ marginBottom: '0.5rem', textAlign: msg.role === 'user' ? 'right' : 'left' }}>
            <strong>{msg.role === 'user' ? 'Tú:' : 'IA:'}</strong>
            <p style={{ background: msg.role === 'user' ? '#d1e7ff' : '#f01212ff', padding: '0.5rem', borderRadius: '5px', display: 'inline-block' }}>
              {msg.content}
            </p>
          </div>
        ))}
        {isLoading && <p>IA está pensando...</p>}
        <div ref={messagesEndRef} />
      </div>

      <div style={{ display: 'flex', marginTop: '1rem' }}>
        <input
          type="text"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyPress={(e) => e.key === 'Enter' && handleSend()}
          placeholder="Escribe tu mensaje..."
          style={{ flexGrow: 1, marginRight: '0.5rem' }}
        />
        <button onClick={handleSend} disabled={isLoading}>
          Enviar
        </button>
      </div>
    </div>
  );
};

export default ChatAIView;