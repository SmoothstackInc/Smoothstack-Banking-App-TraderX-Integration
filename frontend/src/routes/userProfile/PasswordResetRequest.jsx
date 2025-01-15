import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { TextField, Typography } from '@mui/material';
import { forgotPassword } from '../../services/AuthService';
import SecondaryBtnComponent from '../../components/buttons/SecondaryBtnComponent';
import generateCaptcha from '../../utils/CaptchaGenerator';

const PasswordResetRequest = () => {
  const [email, setEmail] = useState('');
  const [captchaQuestion, setCaptchaQuestion] = useState('');
  const [captchaAnswer, setCaptchaAnswer] = useState('');
  const [captchaInput, setCaptchaInput] = useState('');
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [showPopup, setShowPopup] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const { question, answer } = generateCaptcha();
    setCaptchaQuestion(question);
    setCaptchaAnswer(answer);
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setErrors({ ...errors, [name]: '' });
    if (name === 'captchaInput') {
      setCaptchaInput(value);
    } else if (name === 'email') {
      setEmail(value);
    } else {
      setErrors({ ...errors, form: '' });
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const inputAsNumber = parseInt(captchaInput);

    if (inputAsNumber !== captchaAnswer) {
      setErrors({ ...errors, captchaInput: 'Incorrect CAPTCHA answer' });
      return;
    }
    setLoading(true);
    try {
      const response = await forgotPassword(email);
      if (response.status === 200) {
        setShowPopup(true);
        setTimeout(() => {
          setShowPopup(false);
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
  };

  useEffect(() => {
    const errorTimeout = setTimeout(() => {
      setErrors({ ...errors, form: '' });
    }, 5000);

    return () => clearTimeout(errorTimeout);
  }, [errors.form]);

  return (
    <div className="password-reset-request">
      <div className="user-container">
        <div className='edit-user-title'>
          <Typography variant="h6">Password Reset</Typography>
        </div>
        <hr />
        <form onSubmit={handleSubmit}>
          <div className="input-user-container">
            <label className="label">Email/Username</label>
            <TextField
              className="inputField"
              name="email"
              value={email}
              onChange={handleChange}
              placeholder="Input your email address"
              required
            />
            {errors.email && <div className="error-details">{errors.email}</div>}
          </div>
          <div className="input-user-container" key="CAPTCHA">
            <label className='label'>CAPTCHA: {captchaQuestion}</label>
            <TextField
              className="inputField"
              type="text"
              name="captchaInput"
              value={captchaInput}
              onChange={handleChange}
              placeholder="Solve the captcha"
              required
            />
            {errors.captchaInput && <div className="error-details">{errors.captchaInput}</div>}
          </div>
          {errors.form && <div className="error-details">{errors.form}</div>}
          <div className='submit-wrapper'>
            <SecondaryBtnComponent type="submit" buttonText={loading ? 'Loading...' : 'Submit'} disabled={loading} />
          </div>
        </form>
        {showPopup && (
          <div className="popup">
            <Typography variant="body1">If your account exists, a recovery email will be sent.</Typography>
          </div>
        )}
      </div>
    </div>
  );
};

export default PasswordResetRequest;
