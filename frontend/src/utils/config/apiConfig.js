export const API_CONFIG = {
  baseURL: import.meta.env.VITE_API_GATEWAY_URL,
  registerEndpoint: '/api/v1/auth/register',
  authenticateEndpoint: '/api/v1/auth/authenticate',
  updateUserDetailsEndpoint: '/api/v1/users',
  deactivateUserEndpoint: '/api/v1/users',
  getUserDetailsEndpoint: '/api/v1/users',
  forgotPasswordEndpoint: '/api/v1/auth/forgot-password',
  resetPasswordEndpoint: '/api/v1/auth/reset-password'
};
