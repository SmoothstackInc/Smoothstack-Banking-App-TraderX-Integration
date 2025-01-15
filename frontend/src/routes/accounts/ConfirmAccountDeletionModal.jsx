import { Box, Button, Modal, TextField, Typography } from '@mui/material';
import React, { useState } from 'react';
import CloseIcon from '@mui/icons-material/Close';
import { ThemeProvider } from '@mui/material/styles';
import theme from './Themes';
const ConfirmAccountDeletionModal = ({ open, handleClose, handleConfirm }) => {
  const [password, setPassword] = useState('');

  const handlePasswordChange = (event) => {
    setPassword(event.target.value);
  };
  const onConfirm = () => {
    handleConfirm(password);
    handleClose();
  };
  return (
    <ThemeProvider theme={theme}>
      <Modal open={open} onClose={handleClose}>
        <Box
          sx={{
            position: 'absolute',
            top: '50%',
            left: '50%',
            transform: 'translate(-50%, -50%)',
            bgcolor: '#1c1c1c',
            boxShadow: 24,
            p: 4,
            display: 'flex',
            flexDirection: 'column',
            gap: 2,
          }}
        >
          <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
            <Typography variant='h5' sx={{ color: 'red', fontWeight: 'bold', textAlign: 'center', mt: 4, padding: 1 }}>
              This Action Cannot Be Undone!
            </Typography>
            <Button variant='contained' sx={{ position: 'absolute', top: 0, right: 0, margin: 2 }}>
              <CloseIcon onClick={handleClose} />
            </Button>
          </Box>
          <Typography variant='h6'>Enter your password to close your account.</Typography>
          <TextField
            label='Enter your password'
            type='password'
            value={password}
            onChange={handlePasswordChange}
            fullWidth
            sx={{ pb: 6 }}
          ></TextField>
          <Button variant='contained' onClick={onConfirm} color='error' disabled={!password}>
            Confirm
          </Button>
        </Box>
      </Modal>
    </ThemeProvider>
  );
};

export default ConfirmAccountDeletionModal;
