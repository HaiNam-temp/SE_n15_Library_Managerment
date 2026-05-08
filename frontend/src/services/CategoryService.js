import axios from 'axios';

const API_BASE = '/api/categories';

export const CategoryService = {
    // Get paginated list of categories
    async getCategories(page = 0, size = 20) {
        const response = await axios.get(`${API_BASE}?pageIndex=${page}&pageSize=${size}`);
        return response.data;
    },

    // Get category detail by ID
    async getCategoryDetail(categoryId) {
        const response = await axios.get(`${API_BASE}/${categoryId}`);
        return response.data;
    },

    // Create new category
    async createCategory(data) {
        const response = await axios.post(API_BASE, data);
        return response.data;
    },

    // Update category by ID
    async updateCategory(categoryId, data) {
        const response = await axios.put(`${API_BASE}/${categoryId}`, data);
        return response.data;
    },

    // Delete category by ID
    async deleteCategory(categoryId) {
        const response = await axios.delete(`${API_BASE}/${categoryId}`);
        return response.data;
    }
};
