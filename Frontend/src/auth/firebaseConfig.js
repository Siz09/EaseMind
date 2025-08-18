import { initializeApp } from 'firebase/app';
import {
  getAuth,
  connectAuthEmulator,
  GoogleAuthProvider,
  EmailAuthProvider,
} from 'firebase/auth';
import { getAnalytics, isSupported } from 'firebase/analytics';

const firebaseConfig = {
  apiKey: import.meta.env.VITE_FIREBASE_API_KEY,
  authDomain: import.meta.env.VITE_FIREBASE_AUTH_DOMAIN,
  databaseURL: import.meta.env.VITE_FIREBASE_DATABASE_URL,
  projectId: import.meta.env.VITE_FIREBASE_PROJECT_ID,
  storageBucket: import.meta.env.VITE_FIREBASE_STORAGE_BUCKET,
  messagingSenderId: import.meta.env.VITE_FIREBASE_MESSAGING_SENDER_ID,
  appId: import.meta.env.VITE_FIREBASE_APP_ID,
  measurementId: import.meta.env.VITE_FIREBASE_MEASUREMENT_ID,
};

// Validate required config
const requiredKeys = ['apiKey', 'authDomain', 'projectId'];
const missingKeys = requiredKeys.filter((key) => !firebaseConfig[key]);

if (missingKeys.length > 0) {
  console.error('Missing Firebase configuration:', missingKeys);
  throw new Error(
    `Missing Firebase environment variables: ${missingKeys.join(', ')}`
  );
}

// Initialize Firebase
const app = initializeApp(firebaseConfig);

// Initialize Auth
export const auth = getAuth(app);

// Connect to Auth emulator in development
if (import.meta.env.DEV && !auth._delegate._isInitialized) {
  try {
    connectAuthEmulator(auth, 'http://localhost:9099', {
      disableWarnings: true,
    });
  } catch (error) {
    // Emulator already connected or not available
    console.log('Auth emulator connection skipped:', error.message);
  }
}

// Initialize Analytics (optional)
let analytics = null;
isSupported().then((supported) => {
  if (supported) {
    analytics = getAnalytics(app);
  }
});

export { analytics };

// Auth providers for easy import
export const googleProvider = new GoogleAuthProvider();
export const emailProvider = new EmailAuthProvider();

// Configure Google provider
googleProvider.setCustomParameters({
  prompt: 'select_account',
});

export default app;
