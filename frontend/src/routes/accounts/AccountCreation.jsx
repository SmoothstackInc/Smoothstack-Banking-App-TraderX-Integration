import OpenAccountForm from './OpenAccountForm';
import { ThemeProvider } from '@mui/material/styles';
import theme from './Themes';
import ReviewApplicationInfo from './ReviewApplicationInfo';
import { useEffect, useState } from 'react';
import apiService from './apiService';
import CustomModal from './CustomModal';
import { useNavigate, useParams } from 'react-router-dom';
import './styles/base.css';
import { useGlobalState } from '../../utils/GlobalStateContext';
import { Stepper, Step, StepLabel, Box } from '@mui/material';

function AccountCreation() {
  const [userData, setUserData] = useState({});
  const {
    globalState: { userId },
  } = useGlobalState();

  const fetchUserDetails = async (userId) => {
    if (!userId) {
      console.warn('Attempted to fetch user details without a valid userId');
      return;
    }
    try {
      const userDetails = await apiService.getUserDetails(userId);
      setUserData(userDetails);
    } catch (error) {
      throw error.response;
    }
  };

  const { accountType } = useParams();
  const [appInfo, setAppInfo] = useState({
    firstName: '',
    lastName: '',
    address1: '',
    address2: '',
    city: '',
    state: '',
    zip: '',
    country: '',
    employment: '',
    income: '',
    ssn: '',
  });
  const [step, setStep] = useState(1);
  const [modalOpen, setModalOpen] = useState(false);
  const [modalMessage, setModalMessage] = useState('');
  const [isSuccess, setIsSuccess] = useState(false);
  const navigate = useNavigate();

  const steps = ['User Information', 'Review Application'];

  const handleUserInfoSubmit = (userData) => {
    setAppInfo(userData);
    setStep(2);
  };

  const handleBack = () => {
    setStep(1);
  };

  const handleConfirm = async () => {
    try {
      const accountData = {
        userId: userId,
        accountType: accountType.toUpperCase(),
        initialBalance: 0.0,
      };
      await apiService.createAccount(accountData);
      setIsSuccess(true);
      setModalMessage('Account Registration Successful!');
      setModalOpen(true);
    } catch (error) {
      setIsSuccess(false);
      setModalMessage('Account Registration Unsuccessful!');
      setModalOpen(true);
    }
  };

  const handleCloseModal = () => {
    setModalOpen(false);
    if (isSuccess) navigate('/accounts'); // Navigate on success
  };

  useEffect(() => {
    fetchUserDetails(userId);
  }, [userId]);

  return (
    <>
      <ThemeProvider theme={theme}>
        <Box sx={{ mt: 8, mb: 3 }}>
          <Stepper activeStep={step - 1} alternativeLabel>
            {steps.map((label, index) => (
              <Step key={label}>
                <StepLabel>{label}</StepLabel>
              </Step>
            ))}
          </Stepper>
        </Box>
        {step === 1 && <OpenAccountForm appInfo={appInfo} onSubmit={handleUserInfoSubmit} userData={userData} />}
        {step === 2 && <ReviewApplicationInfo appInfo={appInfo} onConfirm={handleConfirm} onBack={handleBack} />}
        <CustomModal open={modalOpen} handleClose={handleCloseModal} message={modalMessage} isSuccess={isSuccess} />
      </ThemeProvider>
    </>
  );
}

export default AccountCreation;
