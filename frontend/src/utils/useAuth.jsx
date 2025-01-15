import { decodeToken, isTokenValid } from './TokenUtils';
import Cookies from 'js-cookie';

const useAuth = () => {
    const token = Cookies.get('token');
    const userPayload = token && isTokenValid() ? decodeToken(token) : null;

    const isLoggedIn = !!userPayload;
    const isAdmin = userPayload?.role === 'ADMIN';
    const isCustomer = userPayload?.role === 'CUSTOMER';

    return { isLoggedIn, isAdmin, isCustomer };
};

export default useAuth;
