import React from 'react';

export default function LeftSidebar({ active, onNavigate }) {
  return (
    <aside className="sidebar">
      <div className="brand">Library</div>
      <nav>
        <button
          className={"nav-item " + (active === 'home' ? 'active' : '')}
          onClick={() => onNavigate('home')}
        >
          <span className="icon" aria-hidden>

            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M3 11.5L12 4l9 7.5" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
              <path d="M5 20.5V12h14v8.5" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
            </svg>
          </span>
          Trang chủ
        </button>

        <button
          className={"nav-item " + (active === 'readers' ? 'active' : '')}
          onClick={() => onNavigate('readers')}
        >
          <span className="icon" aria-hidden>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 12a4 4 0 100-8 4 4 0 000 8z" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
              <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
            </svg>
          </span>
          Quản lý độc giả
        </button>

        <button
          className={"nav-item " + (active === 'categories' ? 'active' : '')}
          onClick={() => onNavigate('categories')}
        >
          <span className="icon" aria-hidden>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M4 6h16M4 12h16M4 18h16" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
            </svg>
          </span>
          Quản lý danh mục
        </button>
      </nav>
    </aside>
  );
}

