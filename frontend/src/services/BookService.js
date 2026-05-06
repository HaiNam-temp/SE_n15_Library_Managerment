import axios from 'axios';

const API_BASE = 'http://localhost:8080/api/books';

export const BookService = {
  // Get paginated list of books
  async getBooks(pageIndex = 0, pageSize = 10) {
    const response = await axios.get(`${API_BASE}?pageIndex=${pageIndex}&pageSize=${pageSize}`);
    return response.data;
  },

  // Get book detail by ID
  async getBookDetail(bookId) {
    const response = await axios.get(`${API_BASE}/${bookId}`);
    return response.data;
  },

  // Create new book
  async createBook(data) {
    const response = await axios.post(API_BASE, data);
    return response.data;
  },

  // Update book by ID
  async updateBook(bookId, data) {
    const response = await axios.put(`${API_BASE}/${bookId}`, data);
    return response.data;
  },

  // Delete book by ID
  async deleteBook(bookId) {
    const response = await axios.delete(`${API_BASE}/${bookId}`);
    return response.data;
  }
};
