import React from 'react';
import { useParams } from 'react-router-dom';
import { Container, Paper, Typography, Box, List, ListItem, ListItemText, Button } from '@mui/material';

const ReviewApplicationInfo = ({ appInfo, onConfirm, onBack }) => {
  const { accountType } = useParams();
  const infoItems = [
    { label: 'First Name', value: appInfo.firstName },
    { label: 'Last Name', value: appInfo.lastName },
    { label: 'Address Line 1', value: appInfo.address1 },
    { label: 'Address Line 2', value: appInfo.address2 },
    { label: 'City', value: appInfo.city },
    { label: 'State', value: appInfo.state },
    { label: 'Zip Code', value: appInfo.zip },
    { label: 'Country', value: appInfo.country },
    { label: 'Employment Status', value: appInfo.employment },
    { label: 'Monthly Income', value: appInfo.income },
    { label: 'Social Security Number', value: appInfo.ssn },
    { label: 'Account Type', value: accountType.charAt(0).toUpperCase() + accountType.slice(1) },
  ];

  return (
    <Container maxWidth='md' sx={{ height: '100%', mb: 6 }}>
      <Paper
        variant='outlined'
        sx={{
          p: { xs: 2, md: 3 },
          background: '#262626',
          filter: 'drop-shadow(0px 5px 10px rgba(226, 236, 249, 0.50))',
        }}
      >
        <Typography variant='h4' sx={{ textAlign: 'center', color: 'primary' }} gutterBottom>
          Review Application
        </Typography>
        <List sx={{ bgcolor: '#242424', borderRadius: '4px', overflow: 'hidden' }}>
          {infoItems.map((item, index) => (
            <ListItem key={index} sx={{ borderBottom: '1px solid #333' }}>
              <ListItemText primary={item.label} secondary={item.value} sx={{ bgcolor: '#242424' }} />
            </ListItem>
          ))}
        </List>
        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 3, gap: 2 }}>
          <Button variant='contained' color='primary' onClick={onBack}>
            Back
          </Button>
          <Button variant='contained' color='primary' onClick={onConfirm}>
            Confirm
          </Button>
        </Box>
      </Paper>
    </Container>
  );
};

export default ReviewApplicationInfo;
