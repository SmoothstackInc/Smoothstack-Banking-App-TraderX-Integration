import axios from 'axios';
import { v4 as uuidv4 } from 'uuid';
import Cookies from 'js-cookie';
const BASE_ACCOUNTS_URL = '/api/v1/accounts';
const BASE_TRANSACTIONS_URL = '/api/v1/transactions';
const BASE_AUTH_URL = '/api/v1/auth';
const BASE_USER_URL = '/api/v1/users';
const getAuthToken = () => `Bearer ${Cookies.get('token')}`;

const addAuthTokenInterceptor = (client) => {
  client.interceptors.request.use(
    (config) => {
      const token = getAuthToken();
      if (token) {
        config.headers.Authorization = token;
      }
      return config;
    },
    (error) => {
      return Promise.reject(error);
    }
  );
};
const apiClientA = axios.create({
  baseURL: BASE_ACCOUNTS_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});
const apiClientT = axios.create({
  baseURL: BASE_TRANSACTIONS_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});
const apiClientAuth = axios.create({
  baseURL: BASE_AUTH_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});
const apiClientUser = axios.create({
  baseURL: BASE_USER_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

addAuthTokenInterceptor(apiClientA);
addAuthTokenInterceptor(apiClientT);
addAuthTokenInterceptor(apiClientAuth);
addAuthTokenInterceptor(apiClientUser);

const getTransactionsByAccountId = async (accountId, filters) => {
  const { startDate, endDate, minAmount, maxAmount, transactionTypeString, page, size, sortBy } = filters;
  try {
    const response = await apiClientT.get(`/by-account/${accountId}`, {
      params: {
        page,
        size,
        sortBy: sortBy,
        startDate,
        endDate,
        minAmount,
        maxAmount,
        transactionTypeString,
      },
    });
    const responseData = response.data.data;
    return {
      transactions: responseData.content,
      pagination: {
        size: responseData.size,
        totalPages: responseData.totalPages,
        totalElements: responseData.totalElements,
        pageNumber: responseData.number,
      },
    };
  } catch (error) {
    throw error.response ? error.response.data : new Error('An unknown error occurred');
  }
};

const createAccount = async (accountData) => {
  try {
    const response = await apiClientA.post('', accountData);
    return response.data;
  } catch (error) {
    throw error.response.data;
  }
};

const getAccountsByUser = async (userId) => {
  try {
    const response = await apiClientA.get(`/by-user/${userId}`, {
      params: {
        active: true,
      },
    });
    return response.data;
  } catch (error) {
    throw error.response.data;
  }
};
const getAccountDetails = async (accountId) => {
  try {
    const response = await apiClientA.get(`/details/${accountId}`);
    return response.data;
  } catch (error) {
    throw error.response.data;
  }
};

const getAllTransactionsForAccount = async (accountId) => {
  try {
    const response = await apiClientT.get(`/by-account/${accountId}/all`);
    return response.data;
  } catch (error) {
    throw error.response.data;
  }
};

const depositIntoAccount = async (accountId, amount) => {
  try {
    const depositData = {
      accountId,
      amount,
      // idempotencyKey: uuidv4(), // future implementation
    };
    await apiClientT.post('/deposit', depositData);
  } catch (error) {
    throw error.response.data;
  }
};
const disableAccount = async (accountId, username, password) => {
  const authData = {
    username,
    password,
  };

  try {
    const response = await apiClientAuth.post('/authenticate', authData, {
      headers: {
        'Content-Type': 'application/json',
      },
    });
    const jwtToken = response.data.token;
    if (jwtToken) {
      const disableResponse = await apiClientA.patch(`/deactivate/by-account/${accountId}`);
      return disableResponse.data;
    } else {
      throw new Error('Invalid password');
    }
  } catch (error) {
    console.error('Error in disableAccount:', error);
    throw error.response;
  }
};

const transferFunds = async (sourceAccountId, targetAccountId, amount) => {
  try {
    const transactionData = {
      sourceAccountId,
      targetAccountId,
      amount,
    };
    await apiClientT.post('/transfer', transactionData);
  } catch (error) {
    throw error.response.data;
  }
};

const getUserDetails = async (userId) => {
  try {
    const response = await apiClientUser.get(`/${userId}`, {
      headers: {
        'Content-Type': 'application/json',
      },
    });
    return response.data;
  } catch (error) {
    throw error.response;
  }
};

//download transactions
const downloadTransactions = async (accountId, format, startDate, endDate) => {
  try {
    const response = await apiClientT.get(`/download`, {
      headers: {
        'Content-Type': 'application/json',
      },
      params: {
        accountId,
        format,
        startDate,
        endDate,
      },
      responseType: 'blob',
    });
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `transactions.${format}`);
    document.body.appendChild(link);
    link.click();

    link.parentNode.removeChild(link);
    window.URL.revokeObjectURL(url);
  } catch (error) {
    console.error('Error downloading transactions:', error);
  }
};

export default {
  createAccount,
  getAccountsByUser,
  getAccountDetails,
  getTransactionsByAccountId,
  disableAccount,
  depositIntoAccount,
  transferFunds,
  getAllTransactionsForAccount,
  getUserDetails,
  downloadTransactions,
};
