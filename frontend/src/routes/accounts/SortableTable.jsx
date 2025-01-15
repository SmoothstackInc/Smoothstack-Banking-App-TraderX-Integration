import React, { useState } from 'react';
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Select,
  MenuItem,
  FormControl,
  Button,
  Container,
  Box,
  Typography,
  useMediaQuery,
  Modal,
  Divider,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import selectStyles from './styles/selectedStyles';
import { useTheme } from '@mui/material/styles';
import { Stack } from '@mui/system';

const SortableTable = ({ data }) => {
  const [sortKey, setSortKey] = useState('balanceAsc');
  const [openModal, setOpenModal] = useState(false);
  const navigate = useNavigate();
  const theme = useTheme();
  const isSmallScreen = useMediaQuery(theme.breakpoints.down('sm'));
  const handleOpenModal = () => {
    setOpenModal(true);
  };

  // Function to handle closing the modal
  const handleCloseModal = () => {
    setOpenModal(false);
  };

  const handleAccountTypeSelection = (accountType) => {
    navigate(`/accounts/open/${accountType}`);
    handleCloseModal();
  };

  const renderSelectValue = (value) => {
    if (isSmallScreen) {
      return '...';
    }
    if (value === 'balanceAsc') return 'Sorted by: Balance (Ascending)';
    if (value === 'balanceDesc') return 'Sorted by: Balance (Descending)';
    return value;
  };

  const handleSortChange = (event) => {
    setSortKey(event.target.value);
  };

  const getSortedData = (data, sortKey) => {
    const direction = sortKey.endsWith('Asc') ? 'asc' : 'desc';
    return data.sort((a, b) => {
      const comparison = a.balance - b.balance;
      return direction === 'asc' ? comparison : -comparison;
    });
  };

  const sortedData = getSortedData([...data], sortKey);

  const handleRowClick = (accountId) => {
    navigate(`/accounts/${accountId}`);
  };

  return (
    <Container maxWidth='md' sx={{ height: '100vh' }}>
      <Paper
        sx={{
          display: 'flex',
          flexDirection: 'column',
          my: { xs: 3, md: 6 },
          p: { xs: 2, md: 3 },
          height: { xs: 'auto', sm: '80%' },
          background: '#262626',
          borderTop: '5px solid #ecb300',
        }}
      >
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Box sx={{ flex: 1 }} />
          <Typography variant='h4' sx={{ textAlign: 'center', flex: 1, color: '#ecb300' }}>
            Accounts
          </Typography>
          <FormControl sx={{ my: 3, flex: 1 }}>
            <Select
              value={sortKey}
              onChange={handleSortChange}
              displayEmpty
              inputProps={{ 'aria-label': 'Without label' }}
              renderValue={renderSelectValue}
              sx={{ ...selectStyles, width: isSmallScreen ? 'auto' : '100%' }}
            >
              <MenuItem value='balanceAsc'>Balance (Ascending)</MenuItem>
              <MenuItem value='balanceDesc'>Balance (Descending)</MenuItem>
            </Select>
          </FormControl>
        </Box>
        <Divider sx={{ width: '100%', backgroundColor: '#FFF', mb: 3 }} />
        <TableContainer
          component={Paper}
          sx={{
            maxHeight: '80%',
            '&::-webkit-scrollbar': {
              display: 'none',
            },
          }}
        >
          <Table>
            <TableHead sx={{ backgroundColor: '#181818' }}>
              <TableRow>
                <TableCell>Type</TableCell>
                <TableCell>Balance</TableCell>
                <TableCell>Account Number</TableCell>
                <TableCell>Info</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {sortedData.map((row, index) => (
                <TableRow key={index}>
                  <TableCell>{row.accountType}</TableCell>
                  <TableCell>{`$${row.balance.toFixed(2)}`}</TableCell>
                  <TableCell>{row.accountNumber}</TableCell>
                  <TableCell>
                    <Box sx={{ width: '85px', height: '30px' }}>
                      <Button
                        variant='outlined'
                        color='primary'
                        sx={{
                          width: { sm: '100%', md: '100%' },
                          height: '100%',
                          backgroundColor: '#181818',
                          color: 'white',
                          textTransform: 'none',
                        }}
                        onClick={() => handleRowClick(row.accountId)}
                      >
                        View
                      </Button>
                    </Box>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
        <Box sx={{ flexGrow: 1 }}></Box>
        <Button
          variant='contained'
          sx={{ fontWeight: 'bold', alignSelf: 'flex-end', width: '150px', textTransform: 'none' }}
          onClick={handleOpenModal}
        >
          Open Account
        </Button>
      </Paper>
      <Modal open={openModal} onClose={handleCloseModal}>
        <Box
          sx={{
            position: 'absolute',
            borderTop: '5px solid #ecb300',
            borderRadius: 1,
            top: '50%',
            left: '50%',
            transform: 'translate(-50%, -50%)',
            width: '400px',
            height: '250px',
            bgcolor: '#181818',
            boxShadow: 10,
            p: 3,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            boxShadow: 'inset 0px 0px 2px 0px var(--primary-light)',
          }}
        >
          <Typography variant='h5' sx={{ mb: 3, color: 'white' }}>
            Choose Account Type
          </Typography>
          <Stack direction='row' spacing={2} sx={{ mt: 6 }}>
            <Button
              variant='contained'
              onClick={() => handleAccountTypeSelection('checking')}
              sx={{ fontWeight: 'bold' }}
            >
              Checking
            </Button>
            <Button
              variant='contained'
              onClick={() => handleAccountTypeSelection('savings')}
              sx={{ fontWeight: 'bold' }}
            >
              Savings
            </Button>
          </Stack>
        </Box>
      </Modal>
    </Container>
  );
};

export default SortableTable;
