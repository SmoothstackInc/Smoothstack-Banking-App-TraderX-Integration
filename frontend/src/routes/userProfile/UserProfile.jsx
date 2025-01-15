import React, { useState, useEffect } from 'react';
import { getUserDetails, updateUserDetails } from '../../services/UserService';
import { authenticateUser } from '../../services/AuthService';
import { getUserId } from '../../utils/TokenUtils';
import { validateNotEmpty, validatePhoneNumber, validateAddress } from '../../utils/validators/UserDetailsValidator';
import { validatePassword } from '../../utils/validators/UserAuthFormValidators';
import { getMaxDate } from '../../utils/validators/DateValidator';
import { useGlobalState } from '../../utils/GlobalStateContext';
import ModalPopup from '../../components/confirmation/ModalPopup';
import generateCaptcha from '../../utils/CaptchaGenerator';
import {
  TextField,
  Select,
  MenuItem,
  FormControl,
  Typography,
} from '@mui/material';

function UserProfile() {
  const { globalState } = useGlobalState();
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    dateOfBirth: '',
    phoneNumber: '',
    address: '',
    secretQuestion: '',
    secretAnswer: '',
    email: '',
    captchaInput: '',
    currentPassword: ''
  });
  const [errors, setErrors] = useState({});
  const [captchaQuestion, setCaptchaQuestion] = useState(null);
  const [captchaAnswer, setCaptchaAnswer] = useState('');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalMessage, setModalMessage] = useState('');
  const userId = globalState.userId || getUserId();

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

  function capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
  }

  useEffect(() => {
    const fetchUserData = async () => {
      if (!globalState.userId) {
        console.error('User ID is not defined');
        return;
      }

      try {
        const response = await getUserDetails(userId);
        const { firstName, lastName, dateOfBirth, phoneNumber, address, secretQuestion, secretAnswer, email } = response.data;

        setFormData({
          firstName: firstName || '',
          lastName: lastName || '',
          dateOfBirth: dateOfBirth ? dateOfBirth.split('T')[0] : '',
          phoneNumber: phoneNumber || '',
          address: address || '',
          secretQuestion: secretQuestion || '',
          secretAnswer: secretAnswer || '',
          email: email || '',
          captchaInput: '',
          currentPassword: ''
        });
      } catch (error) {
        console.error('Error fetching user details:', error);
      }
    };

    if (globalState.userId) {
      fetchUserData();
    }

    const { question, answer } = generateCaptcha();
    setCaptchaQuestion(question);
    setCaptchaAnswer(answer);
  }, [globalState.userId]);

  const openModal = (message) => {
    setModalMessage(message);
    setIsModalOpen(true);
  };

  const closeModal = () => setIsModalOpen(false);

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
      default:
        error = validateNotEmpty(String(value));
        break;
    }
    setFormData({ ...formData, [name]: value !== undefined ? value : '' });
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
      currentPassword: validatePassword,
    };

    for (const field in formData) {
      if (formData[field] !== '' && fieldValidators[field]) {
        const error = fieldValidators[field](formData[field]);
        newErrors[field] = error;
        if (error) isValid = false;
      }
    }

    if (!formData.captchaInput.startsWith(String(captchaAnswer)) || formData.captchaInput.length !== String(captchaAnswer).length) {
      newErrors.captchaInput = 'Incorrect CAPTCHA answer. Please try again.';
      isValid = false;
    }

    if (!formData.currentPassword) {
      newErrors.currentPassword = 'Current password is required';
      isValid = false;
    }

    setErrors(newErrors);

    if (isValid) {
      try {
        // Verify current password before updating
        await authenticateUser(globalState.username, formData.currentPassword);

        // Prepare data to be sent to backend
        const updateData = { ...formData };
        console.log("Sending data to backend:", updateData);

        // Update user info
        await updateUserDetails(userId, updateData);
        openModal("Profile details have been successfully updated");
      } catch (error) {
        console.error('Error submitting form:', error);
        if (error.response) {
          // Handle authentication failure
          if (error.response.status === 401) {
            setErrors(prevErrors => ({ ...prevErrors, currentPassword: 'Incorrect current password.' }));
            openModal('Authentication error: Invalid password');
          } else if (error.response.data && error.response.data.message) {
            const errorMessage = error.response.data.message;
            setErrors(prevErrors => ({ ...prevErrors, form: errorMessage }));
            openModal(errorMessage);
          } else {
            setErrors(prevErrors => ({ ...prevErrors, form: 'An unexpected error occurred. Please try again later.' }));
            openModal('An unexpected error occurred. Please try again later.');
          }
        } else {
          setErrors(prevErrors => ({ ...prevErrors, form: 'An error occurred. Please try again.' }));
          openModal('An error occurred. Please try again.');
        }
      }
    }
  };

  return (
    <div id="user-profile" className="user-profile">
      <div className="details-wrapper">
        <div className="user-container">
          <div className='edit-user-title'>
            <Typography variant="h6" >Edit User</Typography>
          </div>
          <hr />
          <form onSubmit={handleSubmit}>
            {Object.keys(formData).map((field) => {
              if (field !== 'captchaInput' && field !== 'currentPassword' && field !== 'newPassword' && field !== 'confirmPassword') {
                return (
                  <div className="input-user-container" key={field}>
                    <label className="label">{capitalizeFirstLetter(field.replace(/([A-Z])/g, ' $1').trim())}</label>
                    {field === 'dateOfBirth' ? (
                      <TextField
                        key={field}
                        id={field}
                        type="date"
                        className="inputField"
                        name="dateOfBirth"
                        value={formData[field]}
                        onChange={handleChange}
                        InputProps={{ inputProps: { max: getMaxDate() } }}
                      />
                    ) : field === 'secretQuestion' ? (
                      <FormControl className="inputField" key={field}>
                        <Select
                          value={formData[field]}
                          onChange={handleChange}
                          name={field}
                        >
                          {secretQuestions.map((question, index) => (
                            <MenuItem key={index} value={question}>{question}</MenuItem>
                          ))}
                        </Select>
                      </FormControl>
                    ) : field === 'secretAnswer' ? (
                      <TextField
                        className="inputField"
                        key={field}
                        id={field}
                        type={field === 'secretAnswer' ? 'password' : 'text'}
                        name={field}
                        value={formData[field]}
                        onChange={handleChange}
                        error={!!errors[field]}
                        helperText={errors[field]}
                      />
                    ) : field === 'email' ? (
                      <TextField
                        className="inputField"
                        type="email"
                        name={field}
                        value={formData[field]}
                        onChange={handleChange}
                      />) : (
                      <TextField
                        className="inputField"
                        type="text"
                        name={field}
                        value={formData[field]}
                        onChange={handleChange}
                      />
                    )}
                    {errors[field] && <div className="error-details">{errors[field]}</div>}
                  </div>
                );
              }
              return null;
            })}
            <div className="input-user-container">
              <label className="label">Current Password (for verification)</label>
              <TextField
                className="inputField"
                type="password"
                name="currentPassword"
                value={formData.currentPassword}
                onChange={handleChange}
                placeholder="Enter your current password"
                required
              />
              {errors.currentPassword && <div className="error-details">{errors.currentPassword}</div>}
            </div>
            <div className="input-user-container" key="CAPTCHA">
              <label className='label'>CAPTCHA: {captchaQuestion}</label>
              <TextField type="text" className='inputField' value={formData.captchaInput} onChange={handleChange} name="captchaInput" placeholder="Solve the captcha"/>
              {errors.captchaInput && <div className="error-details">{errors.captchaInput}</div>}
            </div>
            {errors.form && <div className='error-details'>{errors.form}</div>}
            <div className='submit-wrapper'>
              <button type="submit" className="secondary-btn">Update</button>
            </div>
          </form>
        </div>
      </div>
      <ModalPopup
        message={modalMessage}
        isOpen={isModalOpen}
        onClose={closeModal}
      />
    </div>
  );
}

export default UserProfile;
