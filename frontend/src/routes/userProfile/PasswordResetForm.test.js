import React from 'react';
import { render, screen, fireEvent, act } from '@testing-library/react';
import { MemoryRouter, useLocation } from 'react-router-dom';
import PasswordResetForm from './PasswordResetForm ';
import { resetPassword } from '../../services/AuthService';
import { validatePassword } from '../../utils/validators/UserAuthFormValidators';

jest.mock('../../services/AuthService', () => ({
  resetPassword: jest.fn(() => Promise.resolve({ status: 200, data: 'Password reset successful' })),
}));

const mockNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockNavigate,
  useLocation: jest.fn(),
}));

jest.mock('../../utils/validators/UserAuthFormValidators', () => ({
  validatePassword: jest.fn(),
}));

const mockLocation = {
  search: '?token=test-token',
};
useLocation.mockReturnValue(mockLocation);

describe('PasswordResetForm Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('renders PasswordResetForm without crashing', () => {
    render(
      <MemoryRouter>
        <PasswordResetForm />
      </MemoryRouter>
    );

    expect(screen.getAllByText(/Reset Password/i)[0]).toBeInTheDocument();
  });

  test('initial state is correct', () => {
    render(
      <MemoryRouter>
        <PasswordResetForm />
      </MemoryRouter>
    );

    expect(screen.getByPlaceholderText(/Enter your new password/i).value).toBe('');
    expect(screen.getByPlaceholderText(/Confirm your new password/i).value).toBe('');
  });

  test('displays error messages for invalid inputs', async () => {
    render(
      <MemoryRouter>
        <PasswordResetForm />
      </MemoryRouter>
    );

    const newPasswordInput = screen.getByPlaceholderText(/Enter your new password/i);
    const confirmPasswordInput = screen.getByPlaceholderText(/Confirm your new password/i);
    const submitButton = screen.getAllByText(/Reset Password/i)[1];

    await act(async () => {
      fireEvent.change(newPasswordInput, { target: { value: 'short' } });
      fireEvent.change(confirmPasswordInput, { target: { value: 'g' } });
      fireEvent.click(submitButton);
    });

    expect(validatePassword).toHaveBeenCalledWith('short');
    expect(await screen.getByText((content, element) => content.includes('Passwords do not match') && element.tagName.toLowerCase() === 'div')).toBeInTheDocument();
  });

  test('submits form with valid inputs', async () => {
    validatePassword.mockReturnValueOnce('');

    render(
      <MemoryRouter>
        <PasswordResetForm />
      </MemoryRouter>
    );

    const newPasswordInput = screen.getByPlaceholderText(/Enter your new password/i);
    const confirmPasswordInput = screen.getByPlaceholderText(/Confirm your new password/i);
    const submitButton = screen.getAllByText(/Reset Password/i)[1];

    await act(async () => {
      fireEvent.change(newPasswordInput, { target: { value: 'ValidPassword123!' } });
      fireEvent.change(confirmPasswordInput, { target: { value: 'ValidPassword123!' } });
      fireEvent.click(submitButton);
    });

    expect(resetPassword).toHaveBeenCalledWith('test-token', 'ValidPassword123!');
    expect(await screen.findByText(/Your password has been successfully reset/i)).toBeInTheDocument();
  });

  test('displays error message on failed password reset', async () => {
    resetPassword.mockImplementationOnce(() =>
      Promise.reject({
        response: { data: 'Error resetting password' },
      })
    );

    render(
      <MemoryRouter>
        <PasswordResetForm />
      </MemoryRouter>
    );

    const newPasswordInput = screen.getByPlaceholderText(/Enter your new password/i);
    const confirmPasswordInput = screen.getByPlaceholderText(/Confirm your new password/i);
    const submitButton = screen.getAllByText(/Reset Password/i)[1];

    await act(async () => {
      fireEvent.change(newPasswordInput, { target: { value: 'ValidPassword123!' } });
      fireEvent.change(confirmPasswordInput, { target: { value: 'ValidPassword123!' } });
      fireEvent.click(submitButton);
    });

    expect(await screen.findByText(/Error resetting password/i)).toBeInTheDocument();
  });
});
