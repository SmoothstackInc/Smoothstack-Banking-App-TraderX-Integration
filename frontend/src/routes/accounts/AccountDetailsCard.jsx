import { Box, Button, Stack, Typography } from '@mui/material';
import React, { useState } from 'react';
import { currencyFormatter } from './utils/formatters';
import TransferModal from './TransferModal';
import DepositModal from './DepositModal';
import apiService from './apiService';
function formatDate(dateString) {
  if (!dateString) {
    return 'N/A';
  }
  const date = new Date(dateString);
  return date.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  });
}
const AccountDetailsCard = ({ accountDetails, userAccounts, accountId, refreshData }) => {
  const formattedDate = formatDate(accountDetails?.transactions[0]?.dateTime);
  const [showDepositModal, setShowDepositModal] = useState(false);
  const [showTransferModal, setShowTransferModal] = useState(false);

  const handleOpenDepositModal = () => setShowDepositModal(true);
  const handleCloseDepositModal = () => setShowDepositModal(false);

  const handleDeposit = async (amount, description) => {
    try {
      const result = await apiService.depositIntoAccount(accountId, amount, description);
      refreshData();
    } catch (error) {
      console.error('Deposit Failed: ', error);
    }
  };

  const handleOpenTransferModal = () => setShowTransferModal(true);
  const handleCloseTransferModal = () => setShowTransferModal(false);
  const handleTransfer = async (targetAccountId, amount) => {
    try {
      await apiService.transferFunds(accountId, targetAccountId, amount);
      refreshData();
    } catch (error) {
      console.error('Transfer Failed: ', error);
    }
  };
  console.log(accountDetails);

  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        p: { xs: 2, md: 3 },
        height: '100%',
        background: '#262626',
      }}
    >
      <Typography variant='h6' sx={{ fontWeight: 'bold', mb: 2 }}>
        Account Details: (Account Type: {accountDetails?.accountType})
      </Typography>
      <Typography sx={{ mb: 1 }}>Account Program: {accountDetails?.programName}</Typography>
      <Typography sx={{ mb: 1 }}>Account Number: {accountDetails?.accountNumber}</Typography>
      <Typography sx={{ mb: 1 }}>Last Activity Date: {formattedDate}</Typography>
      <Typography variant='h6' sx={{ mb: 1 }}>
        Balance:{' '}
        <span style={{ color: '#49FE01', fontWeight: '500' }}>{currencyFormatter(accountDetails?.balance)}</span>
      </Typography>
      <Box sx={{ display: 'flex', justifyContent: 'center', width: '100%' }}>
        <Stack spacing={1} sx={{ width: '50%' }}>
          <Button variant='contained' color='primary' onClick={handleOpenDepositModal} sx={{ fontWeight: 'bold' }}>
            Deposit
          </Button>
          <Button variant='contained' color='primary' onClick={handleOpenTransferModal} sx={{ fontWeight: 'bold' }}>
            Transfer
          </Button>
        </Stack>
      </Box>
      <DepositModal
        open={showDepositModal}
        handleClose={handleCloseDepositModal}
        accountDetails={accountDetails}
        handleDeposit={handleDeposit}
      />

      <TransferModal
        open={showTransferModal}
        handleClose={handleCloseTransferModal}
        accountDetails={accountDetails}
        allAccounts={userAccounts}
        handleTransfer={handleTransfer}
        currentAccountId={accountId}
      />
    </Box>
  );
};

export default AccountDetailsCard;
