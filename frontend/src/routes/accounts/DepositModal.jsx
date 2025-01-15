import { Modal, Box, Typography, TextField, Button, Divider } from '@mui/material';
import { useState } from 'react';
import { ThemeProvider } from '@mui/material/styles';
import theme from './Themes';
import { currencyFormatter } from './utils/formatters';
const style = {
  position: 'absolute',
  borderTop: '5px solid #ecb300',
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
  overflow: 'visible',
  borderRadius: 1,
};

const DepositModal = ({ open, handleClose, accountDetails, handleDeposit }) => {
  const [amount, setAmount] = useState('');

  const handleConfirmClick = async () => {
    if (amount) {
      try {
        await handleDeposit(amount, 'User deposit');
      } catch (error) {
        console.error('Deposit failed: ', error);
      }
    }
    handleClose();
  };

  return (
    <ThemeProvider theme={theme}>
      <Modal open={open} onClose={handleClose}>
        <>
          <Box sx={style}>
            <Typography variant='h5' component='h2'>
              Deposit
            </Typography>
            <Divider sx={{ width: 'calc(100% + 64px)', marginLeft: '-32px', backgroundColor: 'white' }} />
            <Typography>Account Number: {accountDetails?.accountNumber}</Typography>
            <Typography>Account Type: {accountDetails?.programName}</Typography>
            <TextField
              label='Amount'
              type='number'
              fullWidth
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
            />
            <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 1 }}>
              <Button
                variant='outlined'
                color='primary'
                sx={{ color: 'white', textTransform: 'none' }}
                onClick={handleClose}
              >
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
        </>
      </Modal>
    </ThemeProvider>
  );
};
export default DepositModal;
