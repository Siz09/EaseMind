import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import * as Sentry from '@sentry/react';

// Initialize Sentry for error tracking (Phase 1 requirement)
Sentry.init({
  dsn: import.meta.env.VITE_SENTRY_DSN || '',
  environment: import.meta.env.MODE,
  integrations: [Sentry.browserTracingIntegration()],
  tracesSampleRate: import.meta.env.DEV ? 1.0 : 0.1,
});

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
