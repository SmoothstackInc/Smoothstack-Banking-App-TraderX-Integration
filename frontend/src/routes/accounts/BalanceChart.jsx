import React from 'react';
import { XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, AreaChart, Area } from 'recharts';
import { processTransactionData, currencyFormatter } from './utils/formatters';
import { Box } from '@mui/material';
const BalanceChart = ({ transactions, accountType }) => {
  const data = processTransactionData(transactions, accountType);

  return (
    <Box style={{ backgroundColor: '#262626', width: '100%', height: '100%' }}>
      <ResponsiveContainer width='100%' height='100%'>
        <AreaChart data={data} margin={{ top: 30, right: 30, left: 30, bottom: 10 }}>
          <defs>
            <linearGradient id='colorBalance' x1='0' y1='0' x2='0' y2='1'>
              <stop offset='5%' stopColor='rgba(152, 255, 212, 0.42)' />
              <stop offset='95%' stopColor='rgba(237, 220, 70, 0.00)' />
            </linearGradient>
          </defs>
          <XAxis dataKey='date' tick={{ fill: '#FFC700' }} />
          <YAxis tick={{ fill: '#FFC700' }} tickFormatter={currencyFormatter} />
          <Tooltip formatter={currencyFormatter} contentStyle={{ backgroundColor: '#262626', color: '#FFF' }} />
          <Legend />
          <Area type='monotone' dataKey='balance' stroke='#FFD700' fillOpacity={1} fill='url(#colorBalance)' />
        </AreaChart>
      </ResponsiveContainer>
    </Box>
  );
};

export default BalanceChart;
