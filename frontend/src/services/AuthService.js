import axios from 'axios';
import { API_CONFIG } from '../utils/config/apiConfig';

export const registerUser = async (userData) => {
  const url = `${API_CONFIG.registerEndpoint}`;
  return axios.post(url, userData);
};

export const authenticateUser = async (username, password) => {
  return axios.post(API_CONFIG.authenticateEndpoint, { username, password });
};

export const forgotPassword = async (email) => {
  return axios.post(API_CONFIG.forgotPasswordEndpoint, { emailOrUsername: email });
};

export const resetPassword = (token, newPassword) => {
  return axios.post(API_CONFIG.resetPasswordEndpoint, { token, newPassword });
};
