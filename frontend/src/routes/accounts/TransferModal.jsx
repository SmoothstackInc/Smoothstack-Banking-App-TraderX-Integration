import { Modal, Box, Typography, TextField, Button, Select, MenuItem, Divider } from '@mui/material';
import { useState } from 'react';
import { ThemeProvider } from '@mui/material/styles';
import theme from './Themes';
import { currencyFormatter } from './utils/formatters';
const style = {
  position: 'absolute',
  borderTop: '5px solid #ecb300',
  borderRadius: 1,
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)',
  width: 400,
  bgcolor: '#1c1c1c',
  boxShadow: 24,
  px: 4,
  py: 2,
  display: 'flex',
  flexDirection: 'column',
  gap: 2,
};

function truncate(number, digits) {
  const stepper = Math.pow(10, digits);
  return Math.floor(number * stepper) / stepper;
}

const TransferModal = ({ open, handleClose, accountDetails, allAccounts, handleTransfer, currentAccountId }) => {
  const [amount, setAmount] = useState('');
  const [selectedAccount, setSelectedAccount] = useState('');
  const [error, setError] = useState('');
  const handleConfirmClick = async () => {
    let transferAmount = parseFloat(amount);
    if (transferAmount > parseFloat(accountDetails.balance.toFixed(2))) {
      setError('Insufficient funds in the source account.');
      return;
    }
    if (transferAmount && selectedAccount) {
      try {
        await handleTransfer(selectedAccount.accountId, transferAmount);
        handleClose();
      } catch (error) {
        console.error('Transfer Failed: ', error);
        setError('Transfer failed. Please try again.');
      }
    }
  };

  const filteredAccounts = allAccounts.filter((account) => account.accountId.toString() !== currentAccountId);

  return (
    <ThemeProvider theme={theme}>
      <Modal open={open} onClose={handleClose}>
        <Box sx={style}>
          <Typography variant='h5' component='h2'>
            Transfer Funds
          </Typography>
          <Divider sx={{ width: 'calc(100% + 64px)', marginLeft: '-32px', backgroundColor: 'white' }} />
          <Typography variant='h6'>From:</Typography>
          <Box sx={{ bgcolor: '#181818', p: 1 }}>
            <Typography>
              {accountDetails?.accountNumber} ({accountDetails?.accountType})
            </Typography>
          </Box>
          <Typography variant='h6' sx={{ mt: 2 }}>
            To Account:
          </Typography>
          <Select fullWidth value={selectedAccount} onChange={(e) => setSelectedAccount(e.target.value)} displayEmpty>
            {filteredAccounts.map((account) => (
              <MenuItem key={account.accountNumber} value={account}>
                {account.accountNumber} ({account.accountType})
              </MenuItem>
            ))}
          </Select>
          <TextField
            label='Amount'
            type='number'
            fullWidth
            value={amount}
            error={!!error}
            helperText={error}
            onChange={(e) => setAmount(e.target.value)}
          />
          <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 1 }}>
            <Button variant='outlined' sx={{ color: 'white', textTransform: 'none' }} onClick={handleClose}>
              Cancel
            </Button>
            <Button
              variant='contained'
              sx={{ fontWeight: 'bold', textTransform: 'none' }}
              onClick={handleConfirmClick}
              color='primary'
            >
              Confirm
            </Button>
          </Box>
        </Box>
      </Modal>
    </ThemeProvider>
  );
};

export default TransferModal;
