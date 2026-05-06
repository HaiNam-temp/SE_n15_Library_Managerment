import React, { useCallback, useEffect, useState } from 'react';
import { ReaderService } from '../services/ReaderService';

export default function ReaderList() {
  const [readers, setReaders] = useState([]);
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
      setErrorMessage(extractErrorMessage(err, 'Không thể tải danh sách độc giả.'));
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
        setSuccessMessage('Cập nhật độc giả thành công.');
      } else {
        await ReaderService.createReader(payload);
        setSuccessMessage('Thêm độc giả thành công.');
      }

      resetForm();
      await fetchReaders(page, size);
    } catch (err) {
      setErrorMessage(extractErrorMessage(err, 'Không thể lưu độc giả.'));
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
      setShowForm(true);
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
      setErrorMessage(extractErrorMessage(err, 'Không thể tải chi tiết độc giả.'));
    }
  }

  async function handleDelete(readerId) {
    const isConfirmed = window.confirm('Bạn có chắc chắn muốn xóa độc giả này?');
    if (!isConfirmed) {
      return;
    }

    setErrorMessage('');
    setSuccessMessage('');
    try {
      await ReaderService.deleteReader(readerId);
      setSuccessMessage('Xóa độc giả thành công.');
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
      setErrorMessage(extractErrorMessage(err, 'Không thể xóa độc giả.'));
    }
  }

  const displayedTotalPages = totalPages || 1;
  const canGoPrev = page > 0;
  const canGoNext = page + 1 < totalPages;

  return (
    <section className="reader-panel">
      <div className="reader-actions">
        <button className="btn btn-primary" onClick={() => { setShowForm(true); setEditingId(null); }}>
          Thêm mới độc giả
        </button>
        <button className="btn" onClick={() => fetchReaders(page, size)}>
          Làm mới danh sách
        </button>
      </div>

      { (showForm || editingId) && (
        <div className="modal" role="dialog" onClick={() => setShowForm(false)}>
          <div className="modal-card form-card" onClick={(e) => e.stopPropagation()}>
            <form className="reader-form" onSubmit={handleSubmit}>
              <h2>{editingId ? 'Cập nhật độc giả' : 'Thêm độc giả'}</h2>

              <div className="form-grid">
                <label>
                  Họ và tên *
                  <input name="fullName" value={form.fullName} onChange={handleInputChange} required />
                </label>

                <label>
                  Email *
                  <input name="email" type="email" value={form.email} onChange={handleInputChange} required />
                </label>

                <label>
                  Mã SV / CCCD *
                  <input
                    name="studentCodeOrCitizenId"
                    value={form.studentCodeOrCitizenId}
                    onChange={handleInputChange}
                    required
                  />
                </label>

                <label>
                  Số điện thoại
                  <input name="phone" value={form.phone} onChange={handleInputChange} />
                </label>

                <label>
                  Giới tính
                  <input name="gender" value={form.gender} onChange={handleInputChange} />
                </label>

                <label>
                  Ngày sinh
                  <input name="dateOfBirth" type="date" value={form.dateOfBirth} onChange={handleInputChange} />
                </label>

                <label className="full-width">
                  Địa chỉ
                  <input name="address" value={form.address} onChange={handleInputChange} />
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
        <button className="btn" onClick={() => fetchReaders(page, size)} disabled={isLoading}>
          {isLoading ? 'Đang tải...' : 'Tải lại'}
        </button>
      </div>

      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>#</th>
              <th>Họ tên</th>
              <th>Email</th>
              <th>Ma SV / CCCD</th>
              <th>Trạng thái</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            {readers.length === 0 ? (
              <tr>
                <td colSpan={6} className="empty-row">
                    Không có dữ liệu độc giả.
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
                      <button className="btn btn-ghost" onClick={() => setViewing(r)} title="Xem chi tiết">
                        <span className="icon">🔍</span>
                        Xem
                      </button>
                      <button className="btn btn-secondary" onClick={() => handleEdit(r.id)} title="Sửa">
                        <span className="icon">✏️</span>
                        Sửa
                      </button>
                      <button className="btn btn-danger" onClick={() => handleDelete(r.id)} title="Xóa">
                        <span className="icon">🗑️</span>
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

      {viewing && (
        <div className="modal" role="dialog" onClick={() => setViewing(null)}>
          <div className="modal-card" onClick={(e) => e.stopPropagation()}>
            <h3>Chi tiết độc giả</h3>
            <dl>
              <dt>Họ và tên</dt>
              <dd>{viewing.fullName}</dd>
              <dt>Email</dt>
              <dd>{viewing.email}</dd>
              <dt>Ma SV / CCCD</dt>
              <dd>{viewing.studentCodeOrCitizenId}</dd>
              <dt>Số điện thoại</dt>
              <dd>{viewing.phone}</dd>
              <dt>Địa chỉ</dt>
              <dd>{viewing.address}</dd>
            </dl>
            <div style={{ textAlign: 'right' }}>
              <button className="btn" onClick={() => setViewing(null)}>Đóng</button>
            </div>
          </div>
        </div>
      )}
    </section>
  );
}
