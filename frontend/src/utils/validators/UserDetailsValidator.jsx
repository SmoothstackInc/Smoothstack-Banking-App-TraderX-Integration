export const validateNotEmpty = (value) => value.trim() ? '' : 'This field cannot be empty';
export const validatePhoneNumber = (value) => /^(\+\d{1,3}[- ]?)?\d{10}$/.test(value) ? '' : 'Invalid phone number';
export const validateAddress = (value) => {
    if (value.length < 7) return 'Address is too short';
    if (value.length > 50) return 'Invalid address';
    return '';
  };