import React, { useEffect, useState } from 'react';
import SortableTable from './SortableTable';
import apiService from './apiService';
import { ThemeProvider } from '@mui/material/styles';
import theme from './Themes';
import { useGlobalState } from '../../utils/GlobalStateContext';

const AccountsView = () => {
  const [accountsData, setAccountsData] = useState([]);
  const {
    globalState: { userId, username },
  } = useGlobalState();

  useEffect(() => {
    const fetchAccountsData = async () => {
      try {
        const data = await apiService.getAccountsByUser(userId);
        setAccountsData(data.data);
      } catch (error) {
        console.error('Error fetching accounts data:', error);
      }
    };
    fetchAccountsData();
  }, []);
  return (
    <ThemeProvider theme={theme}>
      <SortableTable data={accountsData} />
    </ThemeProvider>
  );
};

export default AccountsView;
