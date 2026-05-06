import axios from 'axios';

const API_BASE = 'http://localhost:8080/api/readers';

export const ReaderService = {
  // Get paginated list of readers
  async getReaders(page = 0, size = 10) {
    const response = await axios.get(`${API_BASE}?page=${page}&size=${size}`);
    return response.data;
  },

  // Get reader detail by ID
  async getReaderDetail(readerId) {
    const response = await axios.get(`${API_BASE}/${readerId}`);
    return response.data;
  },

  // Create new reader
  async createReader(data) {
    const response = await axios.post(API_BASE, data);
    return response.data;
  },

  // Update reader by ID
  async updateReader(readerId, data) {
    const response = await axios.put(`${API_BASE}/${readerId}`, data);
    return response.data;
  },

  // Delete reader by ID
  async deleteReader(readerId) {
    const response = await axios.delete(`${API_BASE}/${readerId}`);
    return response.data;
  }
};
