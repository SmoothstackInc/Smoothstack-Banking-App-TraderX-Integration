import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { TextField, Typography } from '@mui/material';
import SecondaryBtnComponent from '../../components/buttons/SecondaryBtnComponent';
import { validatePassword } from '../../utils/validators/UserAuthFormValidators';
import { validateNotEmpty } from '../../utils/validators/UserDetailsValidator';
import { resetPassword } from '../../services/AuthService';

const PasswordResetForm = () => {
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');
  const navigate = useNavigate();
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search); 
  const token = searchParams.get('token');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setErrors({ ...errors, [name]: '' });
    if (name === 'newPassword') {
      setNewPassword(value);
      const passwordError = validatePassword(value);
      setErrors((prevErrors) => ({ ...prevErrors, newPassword: passwordError }));
    } else if (name === 'confirmPassword') {
      setConfirmPassword(value);
    } else {
      setErrors({ ...errors, form: '' });
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const newErrors = {};

    const passwordError = validatePassword(newPassword);
    if (passwordError) {
      newErrors.newPassword = passwordError;
    }

    const confirmPasswordError = validateNotEmpty(confirmPassword);
    if (confirmPassword !== newPassword || confirmPasswordError) {
      newErrors.confirmPassword = 'Passwords do not match';
    }

    setErrors(newErrors);

    if (Object.keys(newErrors).length === 0) {
      setLoading(true);
      try {
        const response = await resetPassword(token, newPassword);
        if (response.status === 200) {
          setSuccessMessage('Your password has been successfully reset');
          setTimeout(() => {
            navigate('/');
          }, 5000);
        } else {
          setErrors({ form: response.data });
        }
      } catch (error) {
        if (error.response && error.response.data) {
          setErrors({ form: error.response.data });
        } else {
          setErrors({ form: 'An unexpected error occurred. Please try again later.' });
        }
      } finally {
        setLoading(false);
      }
    }
  };

  useEffect(() => {
    const errorTimeout = setTimeout(() => {
      setErrors({ ...errors, form: '' });
    }, 3000);

    return () => clearTimeout(errorTimeout);
  }, [errors.form]);

  return (
    <div className="password-reset">
      <div className="user-container">
        <div className='edit-user-title'>
          <Typography variant="h6">Reset Password</Typography>
        </div>
        <hr />
        {successMessage && <div className="success">{successMessage}</div>}
        <form onSubmit={handleSubmit}>
          {errors.form && <div className="error">{errors.form}</div>}
          <div className="input-user-container">
            <label className="label">New Password</label>
            <TextField
              className="inputField"
              name="newPassword"
              value={newPassword}
              onChange={handleChange}
              type="password"
              placeholder="Enter your new password"
              required
            />
            {errors.newPassword && <div className="error-details">{errors.newPassword}</div>}
          </div>
          <div className="input-user-container">
            <label className="label">Confirm Password</label>
            <TextField
              className="inputField"
              name="confirmPassword"
              value={confirmPassword}
              onChange={handleChange}
              type="password"
              placeholder="Confirm your new password"
              required
            />
            {errors.confirmPassword && <div className="error-details">{errors.confirmPassword}</div>}
          </div>
          <div className='submit-wrapper'>
            <SecondaryBtnComponent type="submit" buttonText={loading ? 'Loading...' : 'Reset Password'} disabled={loading} />
          </div>
        </form>
      </div>
    </div>
  );
};

export default PasswordResetForm;
