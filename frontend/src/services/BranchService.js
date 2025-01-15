import axios from 'axios';
import Cookies from 'js-cookie';

const axiosInstance = axios.create({
  baseURL: '/api/v1', // host better as variable so we can change in one location
  headers: {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${Cookies.get('token')}`,
  },
});

export const fetchBranches = async (page) => {
  try {
    const response = await axiosInstance.get('/branch', {
      params: { page },
    });

    return {
      branches: Array.isArray(response.data) ? response.data : [],
      totalPages: response.data.totalPages ? response.data.totalPages : 1,
    };
  } catch (error) {
    console.error('Error fetching branches:', error);
    return { branches: [], totalPages: 1 };
  }
};

export const fetchBankers = async (branchId) => {
  try {
    const response = await axiosInstance.get('/banker', {
      params: { branchId },
    });

    const bankersData = response.data || [];
    return { bankers: bankersData };
  } catch (error) {
    console.error('Error fetching bankers:', error);
    return { bankers: [] };
  }
};