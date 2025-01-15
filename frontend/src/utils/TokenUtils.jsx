import Cookies from 'js-cookie';

export const decodeToken = (token) => {
    const [headerBase64, payloadBase64, signature] = token.split('.');
    const payload = JSON.parse(window.atob(payloadBase64));
    return payload;
};

export const getTokenPayload = () => {
    const token = Cookies.get('token');
    if (!token) return null;
    return decodeToken(token);
};

export const getUserRole = () => {
    const payload = getTokenPayload();
    return payload ? payload.role : null;
};

export const getUserId = () => {
    const payload = getTokenPayload();
    return payload ? payload.userId : null;
};

export const getUsername = () => {
    const payload = getTokenPayload();
    return payload ? payload.sub : null;
};

export const isTokenValid = () => {
    const payload = getTokenPayload();
    if (!payload) return false;

    try {
        const { exp } = payload;
        return Date.now() <= exp * 1000;
    } catch (error) {
        return false;
    }
};
