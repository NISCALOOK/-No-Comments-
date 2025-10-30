import React, { useLayoutEffect, useRef } from 'react';
import './WelcomePage.css';

interface WelcomePageProps {
  onLoginClick: () => void;
  onRegisterClick: () => void;
}

const WelcomePage: React.FC<WelcomePageProps> = ({ onLoginClick, onRegisterClick }) => {
  const canvasRef = useRef<HTMLCanvasElement>(null);

  useLayoutEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) {
      console.error('Canvas element not found');
      return;
    }

    const ctx = canvas.getContext('2d');
    if (!ctx) {
      console.error('Canvas context not available');
      return;
    }

    // Configurar tamaño del canvas
    const handleResize = () => {
      canvas.width = window.innerWidth;
      canvas.height = window.innerHeight;
    };

    handleResize();
    window.addEventListener('resize', handleResize);

    // Clase Neuron
    class Neuron {
      x: number;
      y: number;
      vx: number;
      vy: number;
      radius: number;
      pulsePhase: number;

      constructor() {
        this.x = Math.random() * canvas.width;
        this.y = Math.random() * canvas.height;
        this.vx = (Math.random() - 0.5) * 0.5;
        this.vy = (Math.random() - 0.5) * 0.5;
        this.radius = Math.random() * 3 + 1;
        this.pulsePhase = Math.random() * Math.PI * 2;
      }

      update() {
        this.x += this.vx;
        this.y += this.vy;
        this.pulsePhase += 0.05;
        
        // Rebotar en las paredes
        if (this.x < 0 || this.x > canvas.width) this.vx *= -1;
        if (this.y < 0 || this.y > canvas.height) this.vy *= -1;
        
        // Mantener dentro de los límites
        this.x = Math.max(0, Math.min(canvas.width, this.x));
        this.y = Math.max(0, Math.min(canvas.height, this.y));
      }

      draw() {
        const pulse = Math.sin(this.pulsePhase) * 0.5 + 0.5;
        const currentRadius = this.radius + pulse * 2;
        
        // Efecto de brillo
        const gradient = ctx.createRadialGradient(
          this.x, this.y, 0,
          this.x, this.y, currentRadius * 3
        );
        gradient.addColorStop(0, 'rgba(255, 0, 0, 0.8)');
        gradient.addColorStop(0.5, 'rgba(255, 0, 0, 0.3)');
        gradient.addColorStop(1, 'rgba(255, 0, 0, 0)');
        
        ctx.fillStyle = gradient;
        ctx.beginPath();
        ctx.arc(this.x, this.y, currentRadius * 3, 0, Math.PI * 2);
        ctx.fill();
        
        // Núcleo
        ctx.fillStyle = '#ff0000';
        ctx.beginPath();
        ctx.arc(this.x, this.y, currentRadius, 0, Math.PI * 2);
        ctx.fill();
      }
    }

    // Crear neuronas
    const neurons: Neuron[] = [];
    const neuronCount = 100;
    for (let i = 0; i < neuronCount; i++) {
      neurons.push(new Neuron());
    }

    // Interacción con el mouse
    let mouseX = 0;
    let mouseY = 0;
    const handleMouseMove = (e: MouseEvent) => {
      mouseX = e.clientX;
      mouseY = e.clientY;
      
      // Crear efecto de pulso
      const pulse = document.createElement('div');
      pulse.className = 'pulse';
      pulse.style.left = mouseX + 'px';
      pulse.style.top = mouseY + 'px';
      document.body.appendChild(pulse);
      setTimeout(() => pulse.remove(), 2000);
      
      // Atraer neuronas cercanas
      neurons.forEach(neuron => {
        const dx = mouseX - neuron.x;
        const dy = mouseY - neuron.y;
        const distance = Math.sqrt(dx * dx + dy * dy);
        if (distance < 100) {
          const force = (100 - distance) / 100;
          neuron.vx += (dx / distance) * force * 0.1;
          neuron.vy += (dy / distance) * force * 0.1;
        }
      });
    };

    canvas.addEventListener('mousemove', handleMouseMove);

    // Dibujar conexiones
    function drawConnections() {
      ctx.strokeStyle = 'rgba(255, 0, 0, 0.1)';
      ctx.lineWidth = 1;
      
      for (let i = 0; i < neurons.length; i++) {
        for (let j = i + 1; j < neurons.length; j++) {
          const dx = neurons[i].x - neurons[j].x;
          const dy = neurons[i].y - neurons[j].y;
          const distance = Math.sqrt(dx * dx + dy * dy);
          
          if (distance < 150) {
            const opacity = 1 - distance / 150;
            ctx.strokeStyle = `rgba(255, 0, 0, ${opacity * 0.2})`;
            ctx.beginPath();
            ctx.moveTo(neurons[i].x, neurons[i].y);
            ctx.lineTo(neurons[j].x, neurons[j].y);
            ctx.stroke();
          }
        }
      }
    }

    // Bucle de animación
    function animate() {
      ctx.fillStyle = 'rgba(0, 0, 0, 0.05)';
      ctx.fillRect(0, 0, canvas.width, canvas.height);
      
      drawConnections();
      neurons.forEach(neuron => {
        neuron.update();
        neuron.draw();
      });
      
      requestAnimationFrame(animate);
    }

    animate();

    // Limpiar al desmontar
    return () => {
      window.removeEventListener('resize', handleResize);
      canvas.removeEventListener('mousemove', handleMouseMove);
    };
  }, []);

  return (
    <div className="welcome-container">
      <canvas 
        ref={canvasRef} 
        className="welcome-canvas"
      />
      
      <div className="welcome-content">
        <h1 className="welcome-title">ClassMate AI</h1>
        <p className="welcome-subtitle">Tu asistente de aprendizaje inteligente</p>
        <div className="welcome-buttons">
          <button className="btn btn-primary" onClick={onLoginClick}>
            Iniciar Sesión
          </button>
          <button className="btn btn-secondary" onClick={onRegisterClick}>
            Registrarse
          </button>
        </div>
      </div>
    </div>
  );
};

export default WelcomePage;