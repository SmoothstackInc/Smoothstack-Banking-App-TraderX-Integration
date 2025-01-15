import React, { useState, useEffect } from 'react';
import { updateUserDetails, deactivateUserAccount } from '../../services/UserService';
import { authenticateUser } from '../../services/AuthService';
import { getUserId } from '../../utils/TokenUtils';
import { useNavigate } from 'react-router-dom';
import { validateNotEmpty } from '../../utils/validators/UserDetailsValidator';
import { validatePassword } from '../../utils/validators/UserAuthFormValidators';
import { useGlobalState } from '../../utils/GlobalStateContext';
import { signOut } from '../../utils/Session';
import ModalPopup from '../../components/confirmation/ModalPopup';
import { TextField, Typography } from '@mui/material';
import SecondaryBtnComponent from '../../components/buttons/SecondaryBtnComponent';
import PrimaryBtnComponent from '../../components/buttons/PrimaryBtnComponent';
import DeleteIcon from '@mui/icons-material/Delete';
import IconButton from '@mui/material/IconButton';
import generateCaptcha from '../../utils/CaptchaGenerator';

function UserSettings() {
    const { globalState, setGlobalState } = useGlobalState();
    const [formData, setFormData] = useState({
        currentPassword: '',
        newPassword: '',
        confirmPassword: '',
        captchaInput: ''
    });
    const [errors, setErrors] = useState({});
    const [captchaQuestion, setCaptchaQuestion] = useState('');
    const [captchaAnswer, setCaptchaAnswer] = useState('');
    const [showDeleteConfirmation, setShowDeleteConfirmation] = useState(false);
    const [deletePassword, setDeletePassword] = useState('');
    const [deletePasswordError, setDeletePasswordError] = useState('');
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [modalMessage, setModalMessage] = useState('');
    const userId = globalState.userId || getUserId();
    const navigate = useNavigate();

    useEffect(() => {
        const { question, answer } = generateCaptcha();
        setCaptchaQuestion(question);
        setCaptchaAnswer(answer);
    }, []);

    const openModal = (message) => {
        setModalMessage(message);
        setIsModalOpen(true);
    };

    const closeModal = () => setIsModalOpen(false);

    const handleChange = (e) => {
        const { name, value } = e.target;
        let error = '';
        switch (name) {
            case 'newPassword':
                error = validatePassword(value);
                break;
            case 'confirmPassword':
                if (value !== formData.newPassword) {
                    error = 'Passwords do not match';
                }
                break;
            default:
                break;
        }
        setFormData({ ...formData, [name]: value !== undefined ? value : '' });
        setErrors({ ...errors, [name]: error });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        const newErrors = {};
        let isValid = true;

        if (!formData.captchaInput.startsWith(String(captchaAnswer)) || formData.captchaInput.length !== String(captchaAnswer).length) {
            newErrors.captchaInput = 'Incorrect CAPTCHA answer. Please try again.';
            isValid = false;
        }

        if (!formData.currentPassword) {
            newErrors.currentPassword = 'Current password is required';
            isValid = false;
        }

        if (formData.newPassword !== formData.confirmPassword) {
            newErrors.confirmPassword = 'Passwords do not match';
            isValid = false;
        }

        setErrors(newErrors);

        if (isValid) {
            try {
                // Verify current password before updating
                await authenticateUser(globalState.username, formData.currentPassword);

                // Prepare data to be sent to backend
                const updateData = { newPassword: formData.newPassword, currentPassword: formData.currentPassword };

                // Update user info
                await updateUserDetails(userId, updateData);
                openModal("Password has been successfully updated");
                setTimeout(closeModal, 2000);
            } catch (error) {
                if (error.response) {
                    // Handle authentication failure
                    if (error.response.status === 401) {
                        setErrors(prevErrors => ({ ...prevErrors, currentPassword: 'Incorrect current password.' }));
                        openModal("Authentication error: Invalid current password.");
                    } else if (error.response.data && error.response.data.message) {
                        openModal(error.response.data.message);
                    } else {
                        openModal('An unexpected error occurred. Please try again later.');
                    }
                } else {
                    openModal('An error occurred. Please try again.');
                }
            }
        }
    };

    const handleDeleteAccount = () => {
        setShowDeleteConfirmation(true);
    };

    const handleCancelDelete = () => {
        setShowDeleteConfirmation(false);
    };

    const handleDeletePasswordChange = (e) => {
        const { value } = e.target;
        setDeletePassword(value);

        const error = validateNotEmpty(value);
        setDeletePasswordError(error);
    };

    const handleConfirmDelete = async () => {
        if (!deletePassword) {
            setDeletePasswordError('Current password is required');
            return;
        }

        try {
            // Verify current password before deleting
            await authenticateUser(globalState.username, deletePassword);

            // Perform the delete operation after password verification
            const payload = { isActive: false, currentPassword: deletePassword };
            await deactivateUserAccount(userId, payload);

            // Close the delete confirmation dialog
            setShowDeleteConfirmation(false);

            // Use a timeout to allow the state update to complete and UI to re-render
            openModal("Your account has been deleted");
            setTimeout(() => {
                signOut(setGlobalState, navigate);
            }, 2000);

        } catch (error) {
            console.error('Error in account deletion process:', error);

            if (error.response && error.response.status === 401) {
                setDeletePasswordError('Incorrect current password.');
            } else {
                // Handle other types of errors
                setErrors(prevErrors => ({
                    ...prevErrors,
                    form: 'An error occurred. Please try again.'
                }));
            }
        }
    };

    return (
        <div id="user-settings" className="user-settings">
            <div className="details-wrapper">
                <div className="user-container">
                    <div className='edit-user-title'>
                        <Typography variant="h6" >User Settings</Typography>
                    </div>
                    <hr />
                    <form onSubmit={handleSubmit}>
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
                            <TextField type="text" className='inputField' value={formData.captchaInput} onChange={handleChange} name="captchaInput" placeholder="Solve the captcha" />
                            {errors.captchaInput && <div className="error-details">{errors.captchaInput}</div>}
                        </div>
                        <div className="input-user-container">
                            <label className="label">New Password</label>
                            <TextField
                                className="inputField"
                                type="password"
                                name="newPassword"
                                value={formData.newPassword}
                                onChange={handleChange}
                                placeholder="Enter a new password"
                                required
                            />
                            {errors.newPassword && <div className="error-details">{errors.newPassword}</div>}
                        </div>
                        <div className="input-user-container">
                            <label className="label">Confirm New Password</label>
                            <TextField
                                className="inputField"
                                type="password"
                                name="confirmPassword"
                                value={formData.confirmPassword}
                                onChange={handleChange}
                                placeholder="Confirm your new password"
                                required
                            />
                            {errors.confirmPassword && <div className="error-details">{errors.confirmPassword}</div>}
                        </div>
                        {errors.form && <div className='error-details'>{errors.form}</div>}
                        <div className='submit-wrapper'>
                            <button type="submit" className="secondary-btn">Update Password</button>
                        </div>
                        <div className="delete-user-account">
                            <IconButton
                                onClick={handleDeleteAccount}
                                className="delete-user-account-button"
                            >
                                <DeleteIcon />
                            </IconButton>
                            {showDeleteConfirmation && (
                                <div className="confirmation-dialog-overlay">
                                    <div className="confirmation-dialog">
                                        <p>Are you sure you want to delete your account? This action cannot be undone.</p>
                                        <TextField
                                            type="password"
                                            className="inputField deleteAccount"
                                            value={deletePassword}
                                            onChange={handleDeletePasswordChange}
                                            placeholder="Enter your current password"
                                            required
                                        />
                                        {deletePasswordError && <div className="error-details delete">{deletePasswordError}</div>}
                                        <section className='user-delete-buttons'>
                                            <PrimaryBtnComponent onClick={handleConfirmDelete} buttonText="Delete" />
                                            <SecondaryBtnComponent onClick={handleCancelDelete} buttonText="Cancel" />
                                        </section>
                                    </div>
                                </div>
                            )}
                        </div>
                    </form>

                    <ModalPopup
                        message={modalMessage}
                        isOpen={isModalOpen}
                        onClose={closeModal}
                    />
                </div>
            </div>
        </div>
    );
}

export default UserSettings;
