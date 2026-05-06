import React, { useCallback, useEffect, useState } from 'react';
import { BookService } from '../services/BookService';
import { CategoryService } from '../services/CategoryService';

export default function BookList() {
  const [books, setBooks] = useState([]);
  const [categories, setCategories] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [isLoading, setIsLoading] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [editingId, setEditingId] = useState(null);
  const [viewing, setViewing] = useState(null);
  const [form, setForm] = useState({
    title: '',
    author: '',
    publishedYear: '',
    publisher: '',
    isbn: '',
    description: '',
    categoryId: ''
  });

  const fetchBooks = useCallback(async (pageIndex, pageSize) => {
    setIsLoading(true);
    setErrorMessage('');
    try {
      const res = await BookService.getBooks(pageIndex, pageSize);
      setBooks(res.data || []);
      setTotalPages(res.totalPages || 0);
    } catch (err) {
      setErrorMessage(extractErrorMessage(err, 'Không thể tải danh sách sách.'));
    } finally {
      setIsLoading(false);
    }
  }, []);

  const fetchCategories = useCallback(async () => {
    try {
      const res = await CategoryService.getCategories(0, 100);
      setCategories(res.data || []);
    } catch (err) {
      console.error('Không thể tải danh mục:', err);
    }
  }, []);

  useEffect(() => {
    fetchBooks(page, size);
    fetchCategories();
  }, [fetchBooks, fetchCategories, page, size]);

  function extractErrorMessage(error, fallback) {
    return error?.response?.data?.message || fallback;
  }

  function resetForm() {
    setForm({
      title: '',
      author: '',
      publishedYear: '',
      publisher: '',
      isbn: '',
      description: '',
      categoryId: ''
    });
    setEditingId(null);
    setShowForm(false);
  }

  function handleInputChange(event) {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setErrorMessage('');
    setSuccessMessage('');
    setIsSaving(true);

    const payload = {
      title: form.title.trim(),
      author: form.author.trim(),
      isbn: form.isbn.trim(),
      publishedYear: form.publishedYear ? parseInt(form.publishedYear) : null,
      publisher: form.publisher.trim() || null,
      description: form.description.trim() || null,
//       categoryId: parseInt(form.categoryId)
      categoryId: form.categoryId ? parseInt(form.categoryId) : null
    };

    try {
      if (editingId) {
        await BookService.updateBook(editingId, payload);
        setSuccessMessage('Cập nhật sách thành công.');
      } else {
        await BookService.createBook(payload);
        setSuccessMessage('Thêm sách thành công.');
      }

      resetForm();
      await fetchBooks(page, size);
    } catch (err) {
      setErrorMessage(extractErrorMessage(err, 'Không thể lưu sách.'));
    } finally {
      setIsSaving(false);
    }
  }

  async function handleEdit(bookId) {
    setErrorMessage('');
    setSuccessMessage('');
    try {
      const detail = await BookService.getBookDetail(bookId);
      setEditingId(bookId);
      setShowForm(true);
      setForm({
        title: detail.title || '',
        author: detail.author || '',
        publishedYear: detail.publishedYear || '',
        publisher: detail.publisher || '',
        isbn: detail.isbn || '',
        description: detail.description || '',
        categoryId: detail.categoryId || ''
      });
    } catch (err) {
      setErrorMessage(extractErrorMessage(err, 'Không thể tải chi tiết sách.'));
    }
  }

  async function handleDelete(bookId) {
    const isConfirmed = window.confirm('Bạn có chắc chắn muốn xóa sách này?');
    if (!isConfirmed) {
      return;
    }

    setErrorMessage('');
    setSuccessMessage('');
    try {
      await BookService.deleteBook(bookId);
      setSuccessMessage('Xóa sách thành công.');
      if (editingId === bookId) {
        resetForm();
      }
      await fetchBooks(page, size);
    } catch (err) {
      setErrorMessage(extractErrorMessage(err, 'Không thể xóa sách.'));
    }
  }

  const displayedTotalPages = totalPages || 1;
  const canGoPrev = page > 0;
  const canGoNext = page + 1 < totalPages;

  return (
    <section className="reader-panel">
      <div className="reader-actions">
        <button className="btn btn-primary" onClick={() => { setShowForm(true); setEditingId(null); }}>
          Thêm mới sách
        </button>
        <button className="btn" onClick={() => fetchBooks(page, size)}>
          Làm mới danh sách
        </button>
      </div>

      {(showForm || editingId) && (
        <div className="modal" role="dialog" onClick={() => setShowForm(false)}>
          <div className="modal-card form-card" onClick={(e) => e.stopPropagation()}>
            <form className="reader-form" onSubmit={handleSubmit}>
              <h2>{editingId ? 'Cập nhật sách' : 'Thêm sách'}</h2>

              <div className="form-grid">
                <label className="full-width">
                  Tên sách *
                  <input name="title" value={form.title} onChange={handleInputChange} required />
                </label>

                <label>
                  Tác giả *
                  <input name="author" value={form.author} onChange={handleInputChange} required />
                </label>

                <label>
                  ISBN *
                  <input name="isbn" value={form.isbn} onChange={handleInputChange} required />
                </label>

                <label>
                  Năm xuất bản
                  <input name="publishedYear" type="number" value={form.publishedYear} onChange={handleInputChange} />
                </label>

                <label>
                  Nhà xuất bản
                  <input name="publisher" value={form.publisher} onChange={handleInputChange} />
                </label>

                <label className="full-width">
                  Danh mục *
                  <select name="categoryId" value={form.categoryId} onChange={handleInputChange} style={{
                    border: '1px solid var(--line)',
                    borderRadius: '8px',
                    padding: '9px 10px',
                    fontSize: '14px'
                  }}>
                    <option value="">Chọn danh mục</option>
                    {categories.map(cat => (
                      <option key={cat.id} value={cat.id}>{cat.categoryName}</option>
                    ))}
                  </select>
                </label>

                <label className="full-width">
                  Mô tả
                  <textarea name="description" value={form.description} onChange={handleInputChange} style={{
                    border: '1px solid var(--line)',
                    borderRadius: '8px',
                    padding: '9px 10px',
                    fontSize: '14px',
                    minHeight: '80px'
                  }} />
                </label>
              </div>

              <div className="form-actions">
                <button className="btn btn-primary" type="submit" disabled={isSaving}>
                  {isSaving ? 'Đang lưu...' : editingId ? 'Lưu cập nhật' : 'Thêm mới'}
                </button>
                <button className="btn" type="button" onClick={resetForm} disabled={isSaving}>
                  Làm mới
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {(errorMessage || successMessage) && (
        <div className="message-box">
          {errorMessage ? <p className="message error">{errorMessage}</p> : null}
          {successMessage ? <p className="message success">{successMessage}</p> : null}
        </div>
      )}

      <div className="toolbar">
        <button className="btn" onClick={() => setPage((p) => Math.max(0, p - 1))} disabled={!canGoPrev || isLoading}>
          Trước
        </button>
        <span>
          Trang {page + 1} / {displayedTotalPages}
        </span>
        <button
          className="btn"
          onClick={() => setPage((p) => p + 1)}
          disabled={!canGoNext || isLoading}
        >
          Tiếp
        </button>
      </div>

      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>#</th>
              <th>Tên sách</th>
              <th>Tác giả</th>
              <th>ISBN</th>
              <th>Danh mục</th>
              <th>Trạng thái</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            {books.length === 0 ? (
              <tr>
                <td colSpan={7} className="empty-row">
                  Không có dữ liệu sách.
                </td>
              </tr>
            ) : (
              books.map((b, idx) => (
                <tr key={b.id}>
                  <td>{idx + 1 + page * size}</td>
                  <td>{b.title}</td>
                  <td>{b.author}</td>
                  <td>{b.isbn}</td>
                  <td>{b.categoryName}</td>
                  <td>{b.status}</td>
                  <td>
                    <div className="row-actions">
                      <button className="btn btn-secondary" onClick={() => handleEdit(b.id)} title="Sửa">
                        Sửa
                      </button>
                      <button className="btn btn-danger" onClick={() => handleDelete(b.id)} title="Xóa">
                        Xóa
                      </button>
                    </div>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </section>
  );
}
