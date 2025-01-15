import React from 'react';
import { render, screen, fireEvent, act } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import SignUpModal from '../../components/signupmodal/SignUpModal';
import { registerUser } from '../../services/AuthService';

jest.mock('../../utils/GlobalStateContext', () => ({
  useGlobalState: () => ({ setGlobalState: jest.fn() })
}));

jest.mock('../../services/AuthService', () => ({
  registerUser: jest.fn(() => Promise.resolve({ data: { token: 'fake-token' } })),
}));

jest.mock('../../utils/config/apiConfig', () => ({}));

test('renders SignUpModal without crashing', () => {
  const handleClose = jest.fn();
  render(
    <MemoryRouter>
      <SignUpModal onClose={handleClose} />
    </MemoryRouter>
  );
  expect(screen.getByText(/register/i)).toBeInTheDocument();
});

test('initial state is correct', () => {
  const handleClose = jest.fn();
  render(
    <MemoryRouter>
      <SignUpModal onClose={handleClose} />
    </MemoryRouter>
  );

  expect(screen.getByLabelText(/username/i).value).toBe('');
  expect(screen.getByLabelText(/email/i).value).toBe('');
  expect(screen.getAllByLabelText(/password/i)[0].value).toBe('');
  expect(screen.getAllByLabelText(/password/i)[1].value).toBe('');
});

test('clicking Sign Up without filling form does not submit', async () => {
  const handleClose = jest.fn();
  render(
    <MemoryRouter>
      <SignUpModal onClose={handleClose} />
    </MemoryRouter>
  );

  const signUpButton = screen.getByText(/sign up/i);

  await act(async () => {
    fireEvent.click(signUpButton);
  });

  expect(registerUser).not.toHaveBeenCalled();
});

test('displays error messages for invalid inputs', async () => {
  const handleClose = jest.fn();
  render(
    <MemoryRouter>
      <SignUpModal onClose={handleClose} />
    </MemoryRouter>
  );

  const usernameInput = screen.getByLabelText(/username/i);
  const passwordInput = screen.getAllByLabelText(/password/i)[0];

  await act(async () => {
    fireEvent.change(usernameInput, { target: { value: 'Da' } });
    fireEvent.blur(usernameInput);
    fireEvent.change(passwordInput, { target: { value: 'short' } });
    fireEvent.blur(passwordInput);
  });

  expect(screen.getByText((content, element) => content.includes('at least 3 characters') && element.tagName.toLowerCase() === 'div')).toBeInTheDocument();
  expect(screen.getByText((content, element) => content.includes('8 to 100 characters') && element.tagName.toLowerCase() === 'pre')).toBeInTheDocument();
});

test('renders SignUpModal and submits form', async () => {
  const handleClose = jest.fn();
  render(
    <MemoryRouter>
      <SignUpModal onClose={handleClose} />
    </MemoryRouter>
  );

  const usernameInput = screen.getByLabelText(/username/i);
  const emailInput = screen.getByLabelText(/email/i);
  const passwordInput = screen.getAllByLabelText(/password/i)[0];
  const confirmPasswordInput = screen.getAllByLabelText(/password/i)[1];
  const signUpButton = screen.getByText(/sign up/i);

  await act(async () => {
    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.change(emailInput, { target: { value: 'testuser@example.com' } });
    fireEvent.change(passwordInput, { target: { value: 'Password123!' } });
    fireEvent.change(confirmPasswordInput, { target: { value: 'Password123!' } });
    fireEvent.click(signUpButton);
  });

  expect(registerUser).toHaveBeenCalledTimes(1);
  expect(registerUser).toHaveBeenCalledWith({
    username: 'testuser',
    email: 'testuser@example.com',
    password: 'Password123!',
  });
});

test('switches to SignInModal when clicking SIGN IN button', async () => {
  const handleClose = jest.fn();
  render(
    <MemoryRouter>
      <SignUpModal onClose={handleClose} />
    </MemoryRouter>
  );

  const signInButton = screen.getByText(/sign in/i);

  await act(async () => {
    fireEvent.click(signInButton);
  });

  expect(screen.getByText(/login/i)).toBeInTheDocument();
});
