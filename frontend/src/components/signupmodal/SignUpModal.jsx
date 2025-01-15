import React, { useEffect, useRef, useState } from 'react';
import Cookies from 'js-cookie';
import { registerUser } from '../../services/AuthService';
import SignInModal from '../signinmodal/SignInModal';
import { useNavigate } from 'react-router-dom';
import { validateUsername, validatePassword } from '../../utils/validators/UserAuthFormValidators';
import { decodeToken } from '../../utils/TokenUtils';
import { useGlobalState } from '../../utils/GlobalStateContext';

const SignUpModal = ({ onClose }) => {
  const [isSigningUp, setIsSigningUp] = useState(true);
  const [username, setUsername] = useState(localStorage.getItem('username') || '');
  const [email, setEmail] = useState(localStorage.getItem('email') || '');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [errors, setErrors] = useState({});
  const modalRef = useRef(null); // hook that allows storing a mutable value that does not cause a re-render when it changes
  const navigate = useNavigate();
  const { setGlobalState } = useGlobalState();

  const switchToSignIn = () => setIsSigningUp(false);
  const switchToSignUp = () => setIsSigningUp(true);

  const handleInputChange = (e) => {
    const { id, value } = e.target;
    let error = '';

    switch (id) {
      case 'username':
        error = validateUsername(value);
        setUsername(value);
        localStorage.setItem('username', value);
        break;
      case 'email':
        setEmail(value);
        localStorage.setItem('email', value);
        break;
      case 'password':
        error = validatePassword(value);
        setPassword(value);
        break;
      case 'confirmPassword':
        setConfirmPassword(value);
        error = value ? (value === password ? '' : 'Passwords do not match') : 'Confirm password cannot be empty';
        break;
      default:
        break;
    }

    setErrors({ ...errors, [id]: error });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const usernameError = validateUsername(username);
    const passwordError = validatePassword(password);
    const confirmPasswordError = confirmPassword ? (confirmPassword === password ? '' : 'Passwords do not match') : 'Confirm password cannot be empty';

    setErrors({ username: usernameError, password: passwordError, confirmPassword: confirmPasswordError });

    if (usernameError || passwordError || confirmPasswordError) {
      return;
    }
    try {
      const response = await registerUser({ username, email, password });
      const token = response.data.token;
      const decodedToken = decodeToken(token);
      const { role, userId, sub: decodedUsername } = decodedToken;

      Cookies.set('token', token, { expires: 1, secure: true });

      setGlobalState({
        userId,
        role,
        username: decodedUsername,
      });

      localStorage.clear();
      onClose();
      navigate('/signup-details');
    } catch (error) {
      if (error.response && error.response.data) {
        setErrors({ form: error.response.data });
      } else {
        console.log(error);
        setErrors({ form: 'An unexpected error occurred. Please try again later.' });
      }
    }
  };

  useEffect(() => {
    const handleOutsideClick = (e) => {
      if (modalRef.current && !modalRef.current.contains(e.target)) {
        onClose();
      }
    };

    document.addEventListener('mousedown', handleOutsideClick);
    return () => document.removeEventListener('mousedown', handleOutsideClick);
  }, [onClose]);

  return (
    <div className='signup-modal' ref={modalRef}>
      {isSigningUp ? (
        <form onSubmit={handleSubmit}>
          <div className='signup-title'>Register</div>
          {errors.form && <div className='error backend'>{errors.form}</div>}
          <div className='form-group'>
            <label htmlFor='username'>Username</label>
            <input
              id='username'
              type='text'
              value={username}
              onChange={handleInputChange}
            />
            {errors.username && (
              <div className='error'>
                Username must be: <br /> {errors.username}
              </div>
            )}
          </div>
          <div className='form-group'>
            <label htmlFor='email'>Email</label>
            <input
              id='email'
              type='email'
              value={email}
              required
              onChange={handleInputChange}
            />
            {errors.email && (
              <pre className='error'>
                Email must contain: <br />
                {errors.email}
              </pre>
            )}
          </div>

          <div className='form-group'>
            <label htmlFor='password'>Password</label>
            <input
              id='password'
              type='password'
              autoComplete='off'
              value={password}
              onChange={handleInputChange}
            />
            {errors.password && (
              <pre className='error'>
                Password must contain: <br />
                {errors.password}
              </pre>
            )}
          </div>
          <div className='form-group'>
            <label htmlFor='confirmPassword'>Confirm Password</label>
            <input
              id='confirmPassword'
              type='password'
              autoComplete='off'
              value={confirmPassword}
              onChange={handleInputChange}
            />
            {errors.confirmPassword && <div className='error' id='confirm'>{errors.confirmPassword}</div>}
          </div>
          <button type='submit'>SIGN UP</button>
          <button type='button' onClick={switchToSignIn}>
            SIGN IN
          </button>
        </form>
      ) : (
        <SignInModal switchToSignUp={switchToSignUp} onClose={onClose} />
      )}
    </div>
  );
};

export default SignUpModal;
