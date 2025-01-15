import React from 'react';
import { createRoot } from 'react-dom/client';
import App from './App';
import { GlobalStateProvider } from './utils/GlobalStateContext';

const root = createRoot(document.getElementById('root'));

root.render(
  <React.StrictMode>
    <GlobalStateProvider>
      <App />
    </GlobalStateProvider>
  </React.StrictMode>
);
