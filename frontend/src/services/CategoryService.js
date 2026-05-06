import axios from 'axios';

const API_BASE = 'http://localhost:8080/api/categories';

export const CategoryService = {
  async getCategories(pageIndex = 0, pageSize = 100) {
    const response = await axios.get(`${API_BASE}?pageIndex=${pageIndex}&pageSize=${pageSize}`);
    return response.data;
  }
};
