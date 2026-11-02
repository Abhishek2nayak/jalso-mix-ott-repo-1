import { APP_NAME } from '@apnaplay/shared';

export default function Home() {
  return (
    <main style={{ padding: '2rem', fontFamily: 'sans-serif' }}>
      <h1>{APP_NAME}</h1>
      <p>Welcome to {APP_NAME} — your streaming platform.</p>
    </main>
  );
}
