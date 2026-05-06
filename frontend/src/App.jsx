import React from 'react';
import ReaderList from './components/ReaderList';

export default function App() {
  return (
    <div className="app">
      <header>
        <h1>Library Manager - Readers</h1>
      </header>
      <main>
        <ReaderList />
      </main>
    </div>
  );
}
