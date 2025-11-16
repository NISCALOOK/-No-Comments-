// vite.config.ts
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'


export default defineConfig({
  plugins: [react()],
  
  server: {
    port: 3000,
    host: true, // Permite acceder desde otras máquinas en la red
  },

 
  test: {
  
    globals: true, 
    environment: 'happy-dom',

   
    setupFiles: ['./src/test/setup.ts'],
  },
})