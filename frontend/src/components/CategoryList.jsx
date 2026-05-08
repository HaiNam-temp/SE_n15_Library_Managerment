import React, { useCallback, useEffect, useState } from 'react';
import { CategoryService } from '../services/CategoryService';

export default function CategoryList() {
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
        categoryName: '',
        description: '',
        status: 'ACTIVE',
        isDefault: false
    });

    const fetchCategories = useCallback(async (pageIndex, pageSize) => {
        setIsLoading(true);
        setErrorMessage('');
        try {
            const res = await CategoryService.getCategories(pageIndex, pageSize);
            setCategories(res.data || []);
            setTotalPages(res.totalPages || 0);
        } catch (err) {
            setErrorMessage(extractErrorMessage(err, 'Không thể tải danh sách danh mục.'));
        } finally {
            setIsLoading(false);
        }
    }, []);

    useEffect(() => {
        fetchCategories(page, size);
    }, [fetchCategories, page, size]);

    function extractErrorMessage(error, fallback) {
        return error?.response?.data?.message || fallback;
    }

    function resetForm() {
        setForm({
            categoryName: '',
            description: '',
            status: 'ACTIVE',
            isDefault: false
        });
        setEditingId(null);
        setShowForm(false);
    }

    function handleInputChange(event) {
        const { name, value, type, checked } = event.target;
        setForm((prev) => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }));
    }

    async function handleSubmit(event) {
        event.preventDefault();
        setErrorMessage('');
        setSuccessMessage('');
        setIsSaving(true);

        const payload = {
            categoryName: form.categoryName.trim(),
            description: form.description.trim() || null,
            status: form.status,
            isDefault: form.isDefault
        };

        try {
            if (editingId) {
                await CategoryService.updateCategory(editingId, payload);
                setSuccessMessage('Cập nhật danh mục thành công.');
            } else {
                await CategoryService.createCategory(payload);
                setSuccessMessage('Thêm danh mục thành công.');
            }

            resetForm();
            await fetchCategories(page, size);
        } catch (err) {
            setErrorMessage(extractErrorMessage(err, 'Không thể lưu danh mục.'));
        } finally {
            setIsSaving(false);
        }
    }

    async function handleEdit(category) {
        setForm({
            categoryName: category.categoryName,
            description: category.description || '',
            status: category.status,
            isDefault: category.isDefault || false
        });
        setEditingId(category.id);
        setShowForm(true);
        setViewing(null);
    }

    async function handleDelete(categoryId) {
        if (!window.confirm('Bạn có chắc chắn muốn xóa danh mục này?')) {
            return;
        }

        setErrorMessage('');
        setSuccessMessage('');
        try {
            await CategoryService.deleteCategory(categoryId);
            setSuccessMessage('Xóa danh mục thành công.');
            await fetchCategories(page, size);
        } catch (err) {
            setErrorMessage(extractErrorMessage(err, 'Không thể xóa danh mục.'));
        }
    }

    async function handleViewDetail(categoryId) {
        try {
            const detail = await CategoryService.getCategoryDetail(categoryId);
            setViewing(detail);
        } catch (err) {
            setErrorMessage(extractErrorMessage(err, 'Không thể tải chi tiết danh mục.'));
        }
    }

    function handlePrevPage() {
        if (page > 0) setPage(page - 1);
    }

    function handleNextPage() {
        if (page < totalPages - 1) setPage(page + 1);
    }

    return (
        <div className="container">
            {/* Messages */}
            {errorMessage && <div className="alert alert-error">{errorMessage}</div>}
            {successMessage && <div className="alert alert-success">{successMessage}</div>}

            {/* Buttons */}
            <div className="button-group">
                <button
                    className="btn btn-primary"
                    onClick={() => {
                        resetForm();
                        setShowForm(true);
                    }}
                >
                    + Thêm danh mục mới
                </button>
            </div>

            {/* Form */}
            {showForm && (
                <div className="card">
                    <h3>{editingId ? 'Cập nhật danh mục' : 'Thêm danh mục mới'}</h3>
                    <form onSubmit={handleSubmit}>
                        <div className="form-group">
                            <label htmlFor="categoryName">Tên danh mục *</label>
                            <input
                                type="text"
                                id="categoryName"
                                name="categoryName"
                                value={form.categoryName}
                                onChange={handleInputChange}
                                placeholder="Nhập tên danh mục"
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="description">Mô tả</label>
                            <textarea
                                id="description"
                                name="description"
                                value={form.description}
                                onChange={handleInputChange}
                                placeholder="Nhập mô tả danh mục"
                                rows="3"
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="status">Trạng thái</label>
                            <select
                                id="status"
                                name="status"
                                value={form.status}
                                onChange={handleInputChange}
                            >
                                <option value="ACTIVE">Hoạt động</option>
                                <option value="INACTIVE">Không hoạt động</option>
                            </select>
                        </div>

                        <div className="form-group">
                            <label>
                                <input
                                    type="checkbox"
                                    name="isDefault"
                                    checked={form.isDefault}
                                    onChange={handleInputChange}
                                />
                                Đây là danh mục mặc định
                            </label>
                        </div>

                        <div className="form-actions">
                            <button type="submit" className="btn btn-success" disabled={isSaving}>
                                {isSaving ? 'Đang lưu...' : 'Lưu'}
                            </button>
                            <button
                                type="button"
                                className="btn btn-secondary"
                                onClick={resetForm}
                                disabled={isSaving}
                            >
                                Hủy
                            </button>
                        </div>
                    </form>
                </div>
            )}

            {/* Detail View */}
            {viewing && (
                <div className="card">
                    <h3>Chi tiết danh mục</h3>
                    <div className="detail-view">
                        <p>
                            <strong>Tên:</strong> {viewing.categoryName}
                        </p>
                        <p>
                            <strong>Mô tả:</strong> {viewing.description || 'N/A'}
                        </p>
                        <p>
                            <strong>Trạng thái:</strong>{' '}
                            <span className={`status-badge status-${viewing.status?.toLowerCase()}`}>
                                {viewing.status === 'ACTIVE' ? 'Hoạt động' : 'Không hoạt động'}
                            </span>
                        </p>
                        <p>
                            <strong>Mặc định:</strong> {viewing.isDefault ? 'Có' : 'Không'}
                        </p>
                        <p>
                            <strong>Ngày tạo:</strong>{' '}
                            {new Date(viewing.createdAt).toLocaleString('vi-VN')}
                        </p>
                        <p>
                            <strong>Cập nhật lần cuối:</strong>{' '}
                            {new Date(viewing.updatedAt).toLocaleString('vi-VN')}
                        </p>
                    </div>
                    <div className="form-actions">
                        <button
                            className="btn btn-primary"
                            onClick={() => {
                                handleEdit(viewing);
                                setViewing(null);
                            }}
                        >
                            Chỉnh sửa
                        </button>
                        <button className="btn btn-secondary" onClick={() => setViewing(null)}>
                            Đóng
                        </button>
                    </div>
                </div>
            )}

            {/* Table */}
            <div className="card">
                <h3>Danh sách danh mục</h3>
                {isLoading ? (
                    <div className="loading">Đang tải dữ liệu...</div>
                ) : categories.length === 0 ? (
                    <div className="empty-state">Không có danh mục nào.</div>
                ) : (
                    <div className="table-wrapper">
                        <table className="table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Tên danh mục</th>
                                    <th>Mô tả</th>
                                    <th>Trạng thái</th>
                                    <th>Mặc định</th>
                                    <th>Hành động</th>
                                </tr>
                            </thead>
                            <tbody>
                                {categories.map((cat) => (
                                    <tr key={cat.id}>
                                        <td>{cat.id}</td>
                                        <td>{cat.categoryName}</td>
                                        <td>{cat.description ? cat.description.substring(0, 30) + '...' : 'N/A'}</td>
                                        <td>
                                            <span className={`status-badge status-${cat.status?.toLowerCase()}`}>
                                                {cat.status === 'ACTIVE' ? 'Hoạt động' : 'Không hoạt động'}
                                            </span>
                                        </td>
                                        <td>{cat.isDefault ? '✓' : ''}</td>
                                        <td>
                                            <button
                                                className="btn btn-sm btn-info"
                                                onClick={() => handleViewDetail(cat.id)}
                                            >
                                                Xem
                                            </button>
                                            <button
                                                className="btn btn-sm btn-warning"
                                                onClick={() => handleEdit(cat)}
                                            >
                                                Sửa
                                            </button>
                                            <button
                                                className="btn btn-sm btn-danger"
                                                onClick={() => handleDelete(cat.id)}
                                            >
                                                Xóa
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>

            {/* Pagination */}
            <div className="pagination">
                <button
                    className="btn btn-sm"
                    onClick={handlePrevPage}
                    disabled={page === 0 || isLoading}
                >
                    ← Trang trước
                </button>
                <span>
                    Trang {page + 1} / {totalPages}
                </span>
                <button
                    className="btn btn-sm"
                    onClick={handleNextPage}
                    disabled={page >= totalPages - 1 || isLoading}
                >
                    Trang sau →
                </button>
            </div>
        </div>
    );
}
