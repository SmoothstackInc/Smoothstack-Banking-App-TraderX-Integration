import React from 'react';
import { render, screen, fireEvent, act } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import SignInModal from '../../components/signinmodal/SignInModal';
import { authenticateUser } from '../../services/AuthService';

jest.mock('../../utils/GlobalStateContext', () => ({
  useGlobalState: () => ({ setGlobalState: jest.fn() })
}));

jest.mock('../../services/AuthService', () => ({
  authenticateUser: jest.fn(() => Promise.resolve({ data: { token: 'fake-token' } })),
}));

jest.mock('../../utils/config/apiConfig', () => ({}));

test('renders SignInModal without crashing', () => {
  const switchToSignUp = jest.fn();
  const handleClose = jest.fn();
  // memory router provides the necessary routing context without affecting the real browser URL.
  render(
    <MemoryRouter> 
      <SignInModal switchToSignUp={switchToSignUp} onClose={handleClose} />
    </MemoryRouter>
  );
  expect(screen.getByText(/login/i)).toBeInTheDocument(); //case insensitive regex
});

test('initial state is correct', () => {
  const switchToSignUp = jest.fn();
  const handleClose = jest.fn();
  render(
    <MemoryRouter>
      <SignInModal switchToSignUp={switchToSignUp} onClose={handleClose} />
    </MemoryRouter>
  );

  expect(screen.getByLabelText(/username/i).value).toBe('');
  expect(screen.getByLabelText(/password/i).value).toBe('');
});

test('clicking Sign In without filling form does not submit', async () => {
  const switchToSignUp = jest.fn();
  const handleClose = jest.fn();
  render(
    <MemoryRouter>
      <SignInModal switchToSignUp={switchToSignUp} onClose={handleClose} />
    </MemoryRouter>
  );

  const signInButton = screen.getByText(/sign in/i);

  await act(async () => {
    fireEvent.click(signInButton);
  });

  expect(authenticateUser).not.toHaveBeenCalled();
});

test('displays error messages for invalid inputs', async () => {
  const switchToSignUp = jest.fn();
  const handleClose = jest.fn();
  render(
    <MemoryRouter>
      <SignInModal switchToSignUp={switchToSignUp} onClose={handleClose} />
    </MemoryRouter>
  );

  const usernameInput = screen.getByLabelText(/username/i);

  await act(async () => {
    fireEvent.change(usernameInput, { target: { value: 'Da' } });
    fireEvent.blur(usernameInput); //simulates the user moving the focus away 
  });

  expect(screen.getByText((content, element) => content.includes('at least 3 characters') && element.tagName.toLowerCase() === 'div')).toBeInTheDocument();
});

test('renders SignInModal and submits form', async () => {
  const switchToSignUp = jest.fn();
  const handleClose = jest.fn();
  render(
    <MemoryRouter>
      <SignInModal switchToSignUp={switchToSignUp} onClose={handleClose} />
    </MemoryRouter>
  );

  const usernameInput = screen.getByLabelText(/username/i);
  const passwordInput = screen.getByLabelText(/password/i);
  const signInButton = screen.getByText(/sign in/i);

  await act(async () => {
    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.change(passwordInput, { target: { value: 'Password123!' } });
    fireEvent.click(signInButton);
  });

  expect(authenticateUser).toHaveBeenCalledTimes(1);
  expect(authenticateUser).toHaveBeenCalledWith('testuser', 'Password123!');
});

test('switches to SignUpModal when clicking SIGN UP button', async () => {
  const switchToSignUp = jest.fn();
  const handleClose = jest.fn();
  render(
    <MemoryRouter>
      <SignInModal switchToSignUp={switchToSignUp} onClose={handleClose} />
    </MemoryRouter>
  );

  const signUpButton = screen.getByText(/sign up/i);

  await act(async () => {
    fireEvent.click(signUpButton);
  });

  expect(switchToSignUp).toHaveBeenCalled();
});
