import axios from 'axios';
import { API_CONFIG } from '../utils/config/apiConfig';
import Cookies from 'js-cookie';

export const getUserDetails = async (userId) => {
    return axios.get(`${API_CONFIG.getUserDetailsEndpoint}/${userId}`, {
        headers: { Authorization: `Bearer ${Cookies.get('token')}` },
    });
};

export const updateUserDetails = async (userId, formData) => {
    return axios.patch(`${API_CONFIG.updateUserDetailsEndpoint}/${userId}`, formData, {
        headers: { Authorization: `Bearer ${Cookies.get('token')}` },
    });
};

export const deactivateUserAccount = async (userId, payload) => {
    return axios.patch(`${API_CONFIG.deactivateUserEndpoint}/${userId}`, payload, {
        headers: { Authorization: `Bearer ${Cookies.get('token')}` },
    });
};