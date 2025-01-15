import React, { useState, useEffect } from 'react';
import { updateUserDetails } from '../../services/UserService';
import { useNavigate } from 'react-router-dom';
import { useGlobalState } from '../../utils/GlobalStateContext';
import { validateNotEmpty, validatePhoneNumber, validateAddress } from '../../utils/validators/UserDetailsValidator';
import { getMaxDate } from '../../utils/validators/DateValidator';
import { getUserId } from '../../utils/TokenUtils';
import SecondaryBtnComponent from '../../components/buttons/SecondaryBtnComponent';
import { TextField, Select, MenuItem, FormControl, Typography } from '@mui/material';
import generateCaptcha from '../../utils/CaptchaGenerator';

function SignupDetails() {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    dateOfBirth: '',
    phoneNumber: '',
    address: '',
    secretQuestion: '',
    secretAnswer: '',
    captchaInput: '',
  });
  const globalState = useGlobalState();
  const [errors, setErrors] = useState({});
  const [captchaQuestion, setCaptchaQuestion] = useState(null);
  const [captchaAnswer, setCaptchaAnswer] = useState('');
  const userId = globalState.userId || getUserId();
  const navigate = useNavigate();

  const secretQuestions = [
    "What is your mother's maiden name?",
    "What was the name of your first pet?",
    "What was the make of your first car?",
    "Where did you go to high school/college?",
    "What is your favorite food?",
    "What is your favorite color?",
    "What is your favorite movie?",
    "What is your favorite book?",
    "What is your favorite sport?",
    "What is your favorite holiday?",
    "What is your favorite song?",
    "What is your favorite hobby?",
    "What is your favorite animal?"
  ];

  useEffect(() => {
    const { question, answer } = generateCaptcha();
    setCaptchaQuestion(question);
    setCaptchaAnswer(answer);
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    let error = '';
    switch (name) {
      case 'phoneNumber':
        error = validatePhoneNumber(value);
        break;
      case 'address':
        error = validateAddress(value);
        break;
      case 'dateOfBirth':
        break;
      default:
        error = validateNotEmpty(String(value));
        break;
    }
    setFormData({ ...formData, [name]: value });
    setErrors({ ...errors, [name]: error });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const newErrors = {};
    let isValid = true;

    const fieldValidators = {
      firstName: validateNotEmpty,
      lastName: validateNotEmpty,
      dateOfBirth: validateNotEmpty,
      phoneNumber: validatePhoneNumber,
      address: validateAddress,
      secretQuestion: validateNotEmpty,
      secretAnswer: validateNotEmpty,
    };

    for (const field in formData) {
      if (field !== 'captchaInput') {
        const validator = fieldValidators[field];
        if (validator) {
          const error = validator(formData[field]);
          newErrors[field] = error;
          if (error) isValid = false;
        }
      }
    }

    if (!formData.captchaInput.startsWith(String(captchaAnswer)) || formData.captchaInput.length !== String(captchaAnswer).length) {
      newErrors.captchaInput = 'Incorrect CAPTCHA answer. Please try again.';
      isValid = false;
    }

    setErrors(newErrors);

    if (isValid) {
      try {
        await updateUserDetails(userId, formData);
        navigate('/dashboard');
      } catch (error) {
        console.error('Error submitting form:', error);
        console.log("form failed:", formData);
      }
    }
  };

  const capitalizeFirstLetter = (string) => {
    return string.charAt(0).toUpperCase() + string.slice(1);
  };

  return (
    <div className="details-wrapper">
      <div className="user-container">
        <div className='edit-user-title'>
          <Typography variant="h6"> Account Setup</Typography>
        </div>
        <hr />
        <form onSubmit={handleSubmit}>
          {Object.keys(formData).map((field) => (
            field !== 'captchaInput' && (
              <div className="input-user-container" key={field}>
                <label className="label">{capitalizeFirstLetter(field.replace(/([A-Z])/g, ' $1').trim())}</label>
                {field === 'dateOfBirth' ? (
                  <TextField
                    className="inputField dateInput"
                    type="date"
                    name={field}
                    value={formData[field]}
                    onChange={handleChange}
                    max={getMaxDate()}
                  />
                ) : field === 'secretQuestion' ? (
                  <FormControl className="inputField" key={field}>
                    <Select
                      className="inputField"
                      name={field}
                      value={formData[field]}
                      onChange={handleChange}
                    >
                      {secretQuestions.map((question, index) => (
                        <MenuItem key={index} value={question}>{question}</MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                ) : (
                  <TextField
                    className="inputField"
                    type={field === 'secretAnswer' ? 'password' : 'text'}
                    name={field}
                    value={formData[field]}
                    onChange={handleChange}
                  />
                )}
                {errors[field] && <div className="error-details">{errors[field]}</div>}
              </div>
            )
          ))}
          <div className="input-user-container" key="CAPTCHA">
            <label className='label'>CAPTCHA: {captchaQuestion}</label>
            <TextField type="text" className='inputField' value={formData.captchaInput} onChange={handleChange} name="captchaInput" />
            {errors.captchaInput && <div className="error-details">{errors.captchaInput}</div>}
          </div>
          <div className='submit-wrapper'>
            <SecondaryBtnComponent type="submit" buttonText="Submit" />
          </div>
        </form>
      </div>
    </div>
  );
}

export default SignupDetails;
