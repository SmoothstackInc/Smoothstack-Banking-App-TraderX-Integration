import React from 'react';
import { Snackbar } from '@mui/material';
import { Alert } from '@mui/material';

export default function AppointmentSnackbar({ open, setOpen }) {
  const handleClose = (event, reason) => {
    if (reason === 'clickaway') {
      return;
    }

    setOpen(false);
  };

  return (
    <Snackbar
      open={open}
      autoHideDuration={6000}
      onClose={handleClose}
      anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
    >
      <Alert onClose={handleClose} severity="success">
        Appointment created successfully!
      </Alert>
    </Snackbar>
  );
}
