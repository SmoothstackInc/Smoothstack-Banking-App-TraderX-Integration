import React from 'react';
import { Modal, Box, Typography, Button } from '@mui/material';

function CustomModal({ open, handleClose, message, navigate, isSuccess }) {
  return (
    <Modal open={open} onClose={handleClose}>
      <Box
        sx={{
          position: 'absolute',
          top: '50%',
          left: '50%',
          transform: 'translate(-50%, -50%)',
          width: 300,
          bgcolor: '#181818',
          boxShadow: 10,
          p: 4,
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          minWidth: '400px',
          minHeight: '200px',
        }}
      >
        <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 4 }}>
          <Typography variant='h6' color={isSuccess ? '#49FE01' : 'error.main'} sx={{ mb: 2 }}>
            {message}
          </Typography>
          <Button variant='contained' onClick={navigate ? () => navigate() : handleClose}>
            Ok
          </Button>
        </Box>
      </Box>
    </Modal>
  );
}

export default CustomModal;
