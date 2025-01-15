import { validateNotEmpty, validatePhoneNumber, validateAddress } from './UserDetailsValidator';

describe('UserDetailsValidator', () => {
  describe('validateNotEmpty', () => {
    it('should return an error message if the value is empty', () => {
      expect(validateNotEmpty('')).toBe('This field cannot be empty');
    });

    it('should return an empty string if the value is not empty', () => {
      expect(validateNotEmpty('not empty')).toBe('');
    });
  });

  describe('validatePhoneNumber', () => {
    it('should return an error message if the phone number is invalid', () => {
      expect(validatePhoneNumber('12345')).toBe('Invalid phone number');
      expect(validatePhoneNumber('12345678901')).toBe('Invalid phone number');
      expect(validatePhoneNumber('+1-234567890')).toBe('Invalid phone number');
    });

    it('should return an empty string if the phone number is valid', () => {
      expect(validatePhoneNumber('1234567890')).toBe('');
    });
  });

  describe('validateAddress', () => {
    it('should return an error message if the address is too short', () => {
      expect(validateAddress('123')).toBe('Address is too short');
    });

    it('should return an error message if the address is too long', () => {
      expect(validateAddress('a'.repeat(51))).toBe('Invalid address');
    });

    it('should return an empty string if the address length is valid', () => {
      expect(validateAddress('123 Main St')).toBe('');
      expect(validateAddress('a'.repeat(7))).toBe('');
      expect(validateAddress('a'.repeat(50))).toBe('');
    });
  });
});
