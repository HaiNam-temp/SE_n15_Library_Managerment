import React, { useCallback, useEffect, useState } from 'react';
import { ReaderService } from '../services/ReaderService';

export default function ReaderList() {
  const [readers, setReaders] = useState([]);
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [isLoading, setIsLoading] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [editingId, setEditingId] = useState(null);
  const [form, setForm] = useState({
    fullName: '',
    email: '',
    studentCodeOrCitizenId: '',
    gender: '',
    address: '',
    phone: '',
    dateOfBirth: ''
  });

  const fetchReaders = useCallback(async (pageIndex, pageSize) => {
    setIsLoading(true);
    setErrorMessage('');
    try {
      const res = await ReaderService.getReaders(pageIndex, pageSize);
      setReaders(res.content || []);
      setTotalPages(res.totalPages || 0);
    } catch (err) {
      setErrorMessage(extractErrorMessage(err, 'Khong the tai danh sach doc gia.'));
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchReaders(page, size);
  }, [fetchReaders, page, size]);

  function extractErrorMessage(error, fallback) {
    return error?.response?.data?.message || fallback;
  }

  function resetForm() {
    setForm({
      fullName: '',
      email: '',
      studentCodeOrCitizenId: '',
      gender: '',
      address: '',
      phone: '',
      dateOfBirth: ''
    });
    setEditingId(null);
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
      fullName: form.fullName.trim(),
      email: form.email.trim(),
      studentCodeOrCitizenId: form.studentCodeOrCitizenId.trim(),
      gender: form.gender.trim() || null,
      address: form.address.trim() || null,
      phone: form.phone.trim() || null,
      dateOfBirth: form.dateOfBirth || null
    };

    try {
      if (editingId) {
        await ReaderService.updateReader(editingId, payload);
        setSuccessMessage('Cap nhat doc gia thanh cong.');
      } else {
        await ReaderService.createReader(payload);
        setSuccessMessage('Them doc gia thanh cong.');
      }

      resetForm();
      await fetchReaders(page, size);
    } catch (err) {
      setErrorMessage(extractErrorMessage(err, 'Khong the luu doc gia.'));
    } finally {
      setIsSaving(false);
    }
  }

  async function handleEdit(readerId) {
    setErrorMessage('');
    setSuccessMessage('');
    try {
      const detail = await ReaderService.getReaderDetail(readerId);
      setEditingId(readerId);
      setForm({
        fullName: detail.fullName || '',
        email: detail.email || '',
        studentCodeOrCitizenId: detail.studentCodeOrCitizenId || '',
        gender: detail.gender || '',
        address: detail.address || '',
        phone: detail.phone || '',
        dateOfBirth: detail.dateOfBirth || ''
      });
    } catch (err) {
      setErrorMessage(extractErrorMessage(err, 'Khong the tai chi tiet doc gia.'));
    }
  }

  async function handleDelete(readerId) {
    const isConfirmed = window.confirm('Ban co chac chan muon xoa doc gia nay?');
    if (!isConfirmed) {
      return;
    }

    setErrorMessage('');
    setSuccessMessage('');
    try {
      await ReaderService.deleteReader(readerId);
      setSuccessMessage('Xoa doc gia thanh cong.');
      if (editingId === readerId) {
        resetForm();
      }

      const shouldMovePrevPage = readers.length === 1 && page > 0;
      const nextPage = shouldMovePrevPage ? page - 1 : page;
      if (nextPage !== page) {
        setPage(nextPage);
      } else {
        await fetchReaders(page, size);
      }
    } catch (err) {
      setErrorMessage(extractErrorMessage(err, 'Khong the xoa doc gia.'));
    }
  }

  const displayedTotalPages = totalPages || 1;
  const canGoPrev = page > 0;
  const canGoNext = page + 1 < totalPages;

  return (
    <section className="reader-panel">
      <form className="reader-form" onSubmit={handleSubmit}>
        <h2>{editingId ? 'Cap nhat doc gia' : 'Them doc gia'}</h2>

        <div className="form-grid">
          <label>
            Ho va ten *
            <input name="fullName" value={form.fullName} onChange={handleInputChange} required />
          </label>

          <label>
            Email *
            <input name="email" type="email" value={form.email} onChange={handleInputChange} required />
          </label>

          <label>
            Ma SV / CCCD *
            <input
              name="studentCodeOrCitizenId"
              value={form.studentCodeOrCitizenId}
              onChange={handleInputChange}
              required
            />
          </label>

          <label>
            So dien thoai
            <input name="phone" value={form.phone} onChange={handleInputChange} />
          </label>

          <label>
            Gioi tinh
            <input name="gender" value={form.gender} onChange={handleInputChange} />
          </label>

          <label>
            Ngay sinh
            <input name="dateOfBirth" type="date" value={form.dateOfBirth} onChange={handleInputChange} />
          </label>

          <label className="full-width">
            Dia chi
            <input name="address" value={form.address} onChange={handleInputChange} />
          </label>
        </div>

        <div className="form-actions">
          <button className="btn btn-primary" type="submit" disabled={isSaving}>
            {isSaving ? 'Dang luu...' : editingId ? 'Luu cap nhat' : 'Them moi'}
          </button>
          <button className="btn" type="button" onClick={resetForm} disabled={isSaving}>
            Lam moi
          </button>
        </div>
      </form>

      {(errorMessage || successMessage) && (
        <div className="message-box">
          {errorMessage ? <p className="message error">{errorMessage}</p> : null}
          {successMessage ? <p className="message success">{successMessage}</p> : null}
        </div>
      )}

      <div className="toolbar">
        <button className="btn" onClick={() => setPage((p) => Math.max(0, p - 1))} disabled={!canGoPrev || isLoading}>
          Prev
        </button>
        <span>
          Page {page + 1} / {displayedTotalPages}
        </span>
        <button
          className="btn"
          onClick={() => setPage((p) => p + 1)}
          disabled={!canGoNext || isLoading}
        >
          Next
        </button>
        <button className="btn" onClick={() => fetchReaders(page, size)} disabled={isLoading}>
          {isLoading ? 'Dang tai...' : 'Tai lai'}
        </button>
      </div>

      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>#</th>
              <th>Ho ten</th>
              <th>Email</th>
              <th>Ma SV / CCCD</th>
              <th>Trang thai</th>
              <th>Thao tac</th>
            </tr>
          </thead>
          <tbody>
            {readers.length === 0 ? (
              <tr>
                <td colSpan={6} className="empty-row">
                  Khong co du lieu doc gia.
                </td>
              </tr>
            ) : (
              readers.map((r, idx) => (
                <tr key={r.id}>
                  <td>{idx + 1 + page * size}</td>
                  <td>{r.fullName}</td>
                  <td>{r.email}</td>
                  <td>{r.studentCodeOrCitizenId}</td>
                  <td>{r.accountStatus || 'N/A'}</td>
                  <td>
                    <div className="row-actions">
                      <button className="btn btn-secondary" onClick={() => handleEdit(r.id)}>
                        Sua
                      </button>
                      <button className="btn btn-danger" onClick={() => handleDelete(r.id)}>
                        Xoa
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
