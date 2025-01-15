import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig(() => {
  return {
    plugins: [react()],
    server: {
      proxy: {
//        '/api/': 'http://host.docker.internal:8765',
         '/api/': 'http://localhost:8765',
      },
      host: '0.0.0.0',
      port: '5173',
      watch: { usePolling: true, interval: 2000 },
    },
  };
});