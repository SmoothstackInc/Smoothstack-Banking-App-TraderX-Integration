import React, { useEffect, useState } from 'react';
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Typography,
  Select,
  MenuItem,
  IconButton,
  TextField,
  Button,
  InputLabel,
  FormControl,
  Box,
  Stack,
  useTheme,
  useMediaQuery,
} from '@mui/material';
import { formatDateTransactions, currencyFormatter } from './utils/formatters';
import NavigateNextIcon from '@mui/icons-material/NavigateNext';
import NavigateBeforeIcon from '@mui/icons-material/NavigateBefore';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import apiService from './apiService';
import dayjs from 'dayjs';
import DownloadModal from './DownloadModal';
const TransactionView = ({ transactions, accountType, setTransactions, accountId, pagination, setPagination }) => {
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [transactionTypeFilter, setTransactionTypeFilter] = useState('');
  const [startDate, setStartDate] = useState(null);
  const [endDate, setEndDate] = useState(null);
  const [minAmount, setMinAmount] = useState('');
  const [maxAmount, setMaxAmount] = useState('');
  const [sortOrder, setSortOrder] = useState('dateTime,desc');
  const theme = useTheme();
  const isSmallScreen = useMediaQuery(theme.breakpoints.down('md'));
  const itemsStart = page * rowsPerPage + 1;
  const itemsEnd = Math.min(itemsStart + rowsPerPage - 1, pagination.totalElements);
  const [downloadModalOpen, setDownloadModalOpen] = useState(false);

  // Handlers for pagination
  const handleNextPage = () => {
    if (page < pagination.totalPages - 1) setPage(page + 1);
  };

  const handlePrevPage = () => {
    if (page > 0) setPage(page - 1);
  };

  const toggleSortOrder = () => {
    setSortOrder((prevOrder) => (prevOrder === 'dateTime,desc' ? 'dateTime,asc' : 'dateTime,desc'));
  };

  const resetFilters = () => {
    setTransactionTypeFilter('');
    setStartDate(null);
    setEndDate(null);
    setMinAmount('');
    setMaxAmount('');
    setPage(0);
  };
  const handleStartDateChange = (newValue) => {
    const formattedDate = newValue ? dayjs(newValue).format('YYYY-MM-DDTHH:mm:ss') : null;
    setStartDate(formattedDate);
  };
  const handleEndDateChange = (newValue) => {
    const formattedDate = newValue ? dayjs(newValue).format('YYYY-MM-DDTHH:mm:ss') : null;
    setEndDate(formattedDate);
  };

  const fetchTransactions = async (resetPage = false) => {
    try {
      const currentPage = resetPage ? 0 : page;

      const filters = {
        startDate,
        endDate,
        minAmount,
        maxAmount,
        transactionTypeString: transactionTypeFilter,
        sortBy: sortOrder,
        page: currentPage,
        size: rowsPerPage,
      };

      const response = await apiService.getTransactionsByAccountId(accountId, filters);
      setTransactions(response.transactions);
      setPagination(response.pagination);
      if (resetPage) {
        setPage(0);
      }
    } catch (error) {
      console.error('Error fetching transactions:', error);
    }
  };

  const handleApplyFilters = () => {
    fetchTransactions(true);
  };
  useEffect(() => {
    fetchTransactions();
  }, [page, rowsPerPage, transactionTypeFilter, startDate, endDate, minAmount, maxAmount, sortOrder]);

  const handleDownload = async (options) => {
    const { downloadStartDate, downloadEndDate, fileType } = options;
    await apiService.downloadTransactions(accountId, fileType, downloadStartDate, downloadEndDate);
  };
  return (
    <>
      <Box
        sx={{
          display: 'flex',
          gap: 2,
          mb: 2,
          alignItems: 'center',
          justifyContent: 'center',
        }}
      >
        <LocalizationProvider dateAdapter={AdapterDayjs}>
          <Stack direction={isSmallScreen ? 'column' : 'row'} gap={1}>
            <DatePicker
              label='Start Date'
              value={startDate}
              onChange={handleStartDateChange}
              sx={{
                width: '150px',
              }}
            />
            <DatePicker
              label='End Date'
              value={endDate}
              onChange={handleEndDateChange}
              shouldDisableDate={(date) => !startDate || date.isBefore(startDate)}
              sx={{ width: '150px' }}
            />
          </Stack>
        </LocalizationProvider>

        <Stack direction={isSmallScreen ? 'column' : 'row'} gap={1}>
          <TextField
            label='Min Amount'
            type='number'
            value={minAmount}
            onChange={(e) => setMinAmount(e.target.value)}
            sx={{ width: '150px' }}
          />
          <TextField
            label='Max Amount'
            type='number'
            value={maxAmount}
            onChange={(e) => setMaxAmount(e.target.value)}
            sx={{ width: '150px' }}
          />
        </Stack>
        <Stack direction={isSmallScreen ? 'column' : 'row'} gap={1}>
          <Button variant='contained' onClick={handleApplyFilters} sx={{ fontWeight: 'bold' }}>
            Apply Filters
          </Button>
          <Button variant='outlined' onClick={resetFilters} sx={{ backgroundColor: '#262626', color: 'white' }}>
            Reset Filters
          </Button>
        </Stack>
      </Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1, alignItems: 'center' }}>
        <Button variant='contained' onClick={() => setDownloadModalOpen(true)} sx={{ fontWeight: 'bold' }}>
          Download
        </Button>
        <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-end', width: '70%' }}>
          {!isSmallScreen && (
            <Typography sx={{ mr: 1 }} variant='h6'>
              Filter by:
            </Typography>
          )}
          <FormControl size='small' sx={{ width: 100 }}>
            <InputLabel>Type</InputLabel>
            <Select
              value={transactionTypeFilter}
              label='Type'
              onChange={(e) => setTransactionTypeFilter(e.target.value)}
            >
              <MenuItem value=''>All</MenuItem>
              <MenuItem value='Deposit'>Deposit</MenuItem>
              <MenuItem value='Withdrawal'>Withdrawal</MenuItem>
              <MenuItem value='Payment'>Payment</MenuItem>
              <MenuItem value='Transfer'>Transfer</MenuItem>
            </Select>
          </FormControl>
          <Button
            variant='outlined'
            sx={{ ml: 1, mr: 2, backgroundColor: '#262626', color: 'white' }}
            onClick={toggleSortOrder}
          >
            Sort by: {sortOrder === 'dateTime,desc' ? 'Most Recent' : 'Oldest'}
          </Button>
        </Box>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Typography>
            {itemsStart} - {itemsEnd} of {pagination.totalElements} items
          </Typography>

          <Box sx={{ display: 'flex' }}>
            <IconButton onClick={handlePrevPage} disabled={page === 0} color='primary'>
              <NavigateBeforeIcon />
            </IconButton>
            <IconButton onClick={handleNextPage} disabled={page >= pagination.totalPages - 1} color='primary'>
              <NavigateNextIcon />
            </IconButton>
          </Box>
        </Box>
      </Box>
      <TableContainer component={Paper} sx={{ py: 1, height: 'auto' }}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Date</TableCell>
              <TableCell>Transaction #</TableCell>
              <TableCell>Description</TableCell>
              <TableCell>Debit</TableCell>
              <TableCell>Credit</TableCell>
              <TableCell>Closing Balance</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {transactions.map((transaction, index) => {
              const { isCredit, amount, closingBalance } = transaction;

              return (
                <TableRow key={index}>
                  <TableCell>{formatDateTransactions(transaction.dateTime)}</TableCell>
                  <TableCell>{transaction.transactionId}</TableCell>
                  <TableCell>{transaction.description}</TableCell>
                  <TableCell sx={{ color: !isCredit ? '#FD4A46' : '' }}>
                    {!isCredit ? currencyFormatter(amount) : '-'}
                  </TableCell>
                  <TableCell sx={{ color: isCredit ? '#49FE01' : '' }}>
                    {isCredit ? currencyFormatter(amount) : '-'}
                  </TableCell>
                  <TableCell>{currencyFormatter(closingBalance)}</TableCell>
                </TableRow>
              );
            })}
          </TableBody>
        </Table>
      </TableContainer>
      <DownloadModal open={downloadModalOpen} onClose={() => setDownloadModalOpen(false)} onDownload={handleDownload} />
    </>
  );
};

export default TransactionView;
