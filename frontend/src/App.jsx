import React, { useState } from 'react';
import LeftSidebar from './components/LeftSidebar';
import ReaderList from './components/ReaderList';
import BookList from './components/BookList';

export default function App() {
  const [view, setView] = useState('home');

  return (
    <div className="app layout">
      <LeftSidebar active={view} onNavigate={setView} />

      <div className="main-content">
        <header>
          <h1>Library Manager</h1>
        </header>

        <main>
          {view === 'home' && (
            <section className="intro card">
              <h2>Giới thiệu hệ thống</h2>
              <p>
                Hệ thống quản lý thư viện cung cấp chức năng quản lý độc giả, sách, mượn trả và báo cáo.
              </p>
            </section>
          )}

          {view === 'readers' && <ReaderList />}
          {view === 'books' && <BookList />}
        </main>
      </div>
    </div>
  );
}
