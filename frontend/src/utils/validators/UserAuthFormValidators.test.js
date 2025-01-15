import { validateUsername, validateEmail, validatePassword } from './UserAuthFormValidators';

describe('UserAuthFormValidators', () => {
  describe('validateUsername', () => {
    it('should return an error message if the username is less than 3 characters', () => {
      expect(validateUsername('ab')).toBe('-at least 3 characters');
    });

    it('should return an empty string if the username is 3 characters or more', () => {
      expect(validateUsername('abc')).toBe('');
      expect(validateUsername('abcd')).toBe('');
    });
  });

  describe('validateEmail', () => {
    it('should return an error message if the email does not contain "@"', () => {
      expect(validateEmail('example.com')).toBe('-one "@" symbol');
    });

    it('should return an error message if the email does not contain "."', () => {
      expect(validateEmail('example@com')).toBe('-at least one "."');
    });

    it('should return an error message if the email does not contain both "@" and "."', () => {
      expect(validateEmail('examplecom')).toBe('-one "@" symbol\n-at least one "."');
    });

    it('should return an empty string if the email contains both "@" and "."', () => {
      expect(validateEmail('example@example.com')).toBe('');
    });
  });

  describe('validatePassword', () => {
    it('should return an error message if the password is less than 8 characters', () => {
      expect(validatePassword('Pass1!')).toBe('-8 to 100 characters');
    });

    it('should return an error message if the password is more than 100 characters', () => {
      expect(validatePassword('P'.repeat(101) + 'a1!')).toBe('-8 to 100 characters');
    });

    it('should return an error message if the password does not contain an uppercase letter', () => {
      expect(validatePassword('password1!')).toBe('-uppercase letters');
    });

    it('should return an error message if the password does not contain a lowercase letter', () => {
      expect(validatePassword('PASSWORD1!')).toBe('-lowercase letters');
    });

    it('should return an error message if the password does not contain a digit', () => {
      expect(validatePassword('Password!')).toBe('-at least one digit');
    });

    it('should return an error message if the password does not contain a special character', () => {
      expect(validatePassword('Password1')).toBe('-special characters');
    });

    it('should return an error message if the password contains spaces', () => {
      expect(validatePassword('Password 1!')).toBe('-no spaces');
    });

    it('should return an empty string if the password is valid', () => {
      expect(validatePassword('Password1!')).toBe('');
    });

    it('should return multiple error messages if the password does not meet multiple criteria', () => {
      expect(validatePassword('pass')).toBe('-8 to 100 characters\n-uppercase letters\n-at least one digit\n-special characters');
    });
  });
});
