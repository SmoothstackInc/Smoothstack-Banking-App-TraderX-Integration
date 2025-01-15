import { Box, Button, Typography } from '@mui/material';
import React from 'react';

const AccountDetailsHeader = ({ handleOpenDeleteModal }) => {
  return (
    <>
      <Box sx={{ display: 'flex', flexDirection: 'row', justifyContent: 'space-between' }}>
        <Typography variant='h4' sx={{ mt: 2 }}>
          Bank Account Details
        </Typography>
        <Box sx={{ display: 'flex', justifyContent: 'flex-start', mb: 2 }}>
          <Button
            variant='outlined'
            color='primary'
            sx={{
              height: '40px',
              borderRadius: 1,
              color: 'red',
              mt: 2,
              width: { xs: '100%', sm: 'auto' },
              '&:hover': {
                backgroundColor: 'red',
                color: 'white',
              },
              textTransform: 'none',
            }}
            onClick={handleOpenDeleteModal}
          >
            Close Account
          </Button>
        </Box>
      </Box>
    </>
  );
};

export default AccountDetailsHeader;
