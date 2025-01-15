import Cookies from 'js-cookie';
import { decodeToken, getUserRole, getUserId, getUsername, isTokenValid } from './TokenUtils';

jest.mock('js-cookie', () => ({
  get: jest.fn(),
}));

const base64Encode = (obj) => Buffer.from(JSON.stringify(obj)).toString('base64');

describe('Token Utility Functions', () => {
  const header = { alg: "HS256", typ: "JWT" };
  const payload = {
    sub: 'username',
    userId: '123',
    role: 'admin',
    exp: Math.floor(Date.now() / 1000) + 3600,
  };

  const mockToken = `${base64Encode(header)}.${base64Encode(payload)}.signature`;

  test('decodeToken should decode the token and return the payload', () => {
    const token = decodeToken(mockToken);
    expect(token).toEqual(payload);
  });

  test('getUserRole should return the role from the token', () => {
    Cookies.get.mockReturnValue(mockToken);
    const role = getUserRole();
    expect(role).toBe('admin');
  });

  test('getUserId should return the userId from the token', () => {
    Cookies.get.mockReturnValue(mockToken);
    const userId = getUserId();
    expect(userId).toBe('123');
  });

  test('getUsername should return the username from the token', () => {
    Cookies.get.mockReturnValue(mockToken);
    const username = getUsername();
    expect(username).toBe('username');
  });

  test('isTokenValid should return true if the token is valid', () => {
    Cookies.get.mockReturnValue(mockToken);
    const isValid = isTokenValid();
    expect(isValid).toBe(true);
  });

  test('isTokenValid should return false if the token is expired', () => {
    const expiredPayload = {
      ...payload,
      exp: Math.floor(Date.now() / 1000) - 3600, // expired 1 hour ago
    };
    const expiredToken = `${base64Encode(header)}.${base64Encode(expiredPayload)}.signature`;

    Cookies.get.mockReturnValue(expiredToken);
    const isValid = isTokenValid();
    expect(isValid).toBe(false);
  });
});
