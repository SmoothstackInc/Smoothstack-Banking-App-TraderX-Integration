import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import apiService from './apiService';
import { Box, Container } from '@mui/material';
import AccountDetailsCard from './AccountDetailsCard';
import BalanceChart from './BalanceChart';
import { ThemeProvider } from '@mui/material/styles';
import theme from './Themes';
import TransactionView from './TransactionView';
import ConfirmAccountDeletionModal from './ConfirmAccountDeletionModal';
import AccountDetailsHeader from './AccountDetailsHeader';
import { useGlobalState } from '../../utils/GlobalStateContext';
import './styles/base.css';
import CustomModal from './CustomModal';
const AccountDetailsView = () => {
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [accountDetails, setAccountDetails] = useState(null);
  const [transactions, setTransactions] = useState(null);
  const [chartTransactions, setChartTransactions] = useState([]);
  const [pagination, setPagination] = useState({});
  const [userAccounts, setUserAccounts] = useState([]);
  const [showSuccessModal, setShowSuccessModal] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');
  const [showErrorModal, setShowErrorModal] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const { accountId } = useParams();
  const handleOpenDeleteModal = () => setShowDeleteModal(true);
  const handleCloseDeleteModal = () => setShowDeleteModal(false);
  const navigate = useNavigate();
  const {
    globalState: { userId, username },
  } = useGlobalState();

  const handleCloseSuccessModal = () => {
    setShowSuccessModal(false);
    navigate('/accounts');
  };

  const handleCloseErrorModal = () => {
    setShowErrorModal(false);
    handleOpenDeleteModal();
  };

  const handleConfirmDelete = async (password) => {
    try {
      await apiService.disableAccount(accountId, username, password);
      handleCloseDeleteModal();
      setSuccessMessage('Account Disabled Successfully');
      setShowSuccessModal(true);
    } catch (error) {
      console.error('Account closure failed:', error);
      setErrorMessage('Invalid Password, please try again');
      setShowErrorModal(true);
    }
  };

  const fetchChartTransactions = async () => {
    try {
      const allTransactionsData = await apiService.getAllTransactionsForAccount(accountId);
      setChartTransactions(allTransactionsData.data);
    } catch (error) {
      console.error('Error fetching chart transactions:', error);
    }
  };

  const fetchAccountDetails = async () => {
    try {
      const accountData = await apiService.getAccountDetails(accountId);
      setAccountDetails(accountData.data);
      const defaultFilters = {
        startDate: null,
        endDate: null,
        minAmount: null,
        maxAmount: null,
        transactionTypeString: '',
        page: 0,
        size: 10,
      };

      const transactionsData = await apiService.getTransactionsByAccountId(accountId, defaultFilters);
      setTransactions(transactionsData.transactions);
      setPagination(transactionsData.pagination);
      if (userId) {
        const accountsData = await apiService.getAccountsByUser(userId);

        const filteredAccounts = accountsData.data.filter(
          (account) =>
            account.accountType.toLowerCase() === 'checking' || account.accountType.toLowerCase() === 'savings'
        );
        setUserAccounts(filteredAccounts);
      }
    } catch (error) {
      console.error('Error fetching data:', error);
    }
  };

  useEffect(() => {
    if (userId) {
      fetchAccountDetails();
      fetchChartTransactions();
    }
  }, [accountId, userId]);

  if (!accountDetails || !transactions) {
    return <div>Loading...</div>; // Add a loading indicator
  }
  return (
    <ThemeProvider theme={theme}>
      <Container maxWidth='100%' sx={{ height: '100%', width: '75vw', mb: 6 }}>
        <AccountDetailsHeader handleOpenDeleteModal={handleOpenDeleteModal} />
        <Box sx={{ display: 'flex', flexDirection: { xs: 'column', sm: 'row' }, gap: 2, overflow: 'hidden' }}>
          <Box sx={{ width: { xs: 'auto', sm: '50%' } }}>
            <AccountDetailsCard
              accountDetails={accountDetails}
              userAccounts={userAccounts}
              accountId={accountId}
              refreshData={fetchAccountDetails}
            />
          </Box>
          <Box sx={{ width: { xs: 'auto', sm: '50%' } }}>
            <BalanceChart transactions={chartTransactions} accountType={accountDetails.accountType} />
          </Box>
        </Box>
        <Box sx={{ py: 2, height: '100%' }}>
          <TransactionView
            accountId={accountId}
            transactions={transactions}
            setTransactions={setTransactions}
            pagination={pagination}
            setPagination={setPagination}
            accountType={accountDetails.accountType}
          />
        </Box>
        <ConfirmAccountDeletionModal
          open={showDeleteModal}
          handleClose={handleCloseDeleteModal}
          handleConfirm={handleConfirmDelete}
        />
      </Container>
      <CustomModal open={showErrorModal} handleClose={handleCloseErrorModal} message={errorMessage} isSuccess={false} />
      <CustomModal
        open={showSuccessModal}
        handleClose={handleCloseSuccessModal}
        message={successMessage}
        isSuccess={true}
      />
    </ThemeProvider>
  );
};

export default AccountDetailsView;
