import React, { useState, useRef, useEffect } from 'react';
import Cookies from 'js-cookie';
import { authenticateUser } from '../../services/AuthService';
import { useGlobalState } from '../../utils/GlobalStateContext';
import { decodeToken } from '../../utils/TokenUtils';
import { validateUsername } from '../../utils/validators/UserAuthFormValidators';
import { useNavigate, Link } from 'react-router-dom';

const SignInModal = ({ switchToSignUp, onClose }) => {
  const navigate = useNavigate();
  const modalRef = useRef(null);
  const { setGlobalState } = useGlobalState();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [errors, setErrors] = useState({});

  const handleUsernameChange = (e) => {
    const newValue = e.target.value;
    setUsername(newValue);
    setErrors({ ...errors, username: validateUsername(newValue) });
  };

  const handlePasswordChange = (e) => {
    const newValue = e.target.value;
    setPassword(newValue);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const usernameError = validateUsername(username);
    setErrors({ username: usernameError });

    if (usernameError) {
      return;
    }
    try {
      const response = await authenticateUser(username, password);
      const token = response.data.token;
      const decodedToken = decodeToken(token);
      const role = decodedToken.role;
      const userId = decodedToken.userId;
      const usernameFromToken = decodedToken.sub;

      Cookies.set('token', token, { expires: 1, secure: false });

      setGlobalState({
        userId,
        role,
        username: usernameFromToken,
      });

      onClose();

      if (role === 'ADMIN') {
        const currentHostname = window.location.hostname;
        window.location.href = `http://${currentHostname}:4200/admin-portal`;
      } else if (role === 'CUSTOMER') {
        navigate('/user-profile', { replace: true });
      }
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
    <div className='signin-modal' ref={modalRef}>
      <form onSubmit={handleSubmit}>
        <div className='signin-title'>Login</div>
        {errors.form && <div className='error backend'>{errors.form}</div>}
        <div className='form-group'>
          <label htmlFor='username'>Username</label>
          <input id='username' type='text' value={username} required onChange={handleUsernameChange} />
          {errors.username && (
            <div className='error'>
              Username must be: <br /> {errors.username}
            </div>
          )}
        </div>

        <div className='form-group'>
          <label htmlFor='password'>Password</label>
          <input id='password' type='password' value={password} required onChange={handlePasswordChange} />
          {errors.password && (
            <pre className='error'>
              Password must contain: <br />
              {errors.password}
            </pre>
          )}
        </div>
        <div className='form-group'>
          <Link to="/password-reset-request" className='forgot-password-link'>
            Forgot Password?
          </Link>
        </div>
        <button type='button' onClick={switchToSignUp}>
          SIGN UP
        </button>
        <button type='submit'>SIGN IN</button>
      </form>
    </div>
  );
};

export default SignInModal;