import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams } from 'react-router-dom';
import Cookies from 'js-cookie';
import './ViewUserLoan.css';

const ViewUserLoan = () => {
  const { userLoanId } = useParams(); 
  const [userLoanInfo, setUserLoanInfo] = useState({});
  const [loanInfo, setLoanInfo] = useState({});
  const [isLoaded, setIsLoaded] = useState(false);

  const token = Cookies.get('token'); 
  const headers = {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${token}`,
  };

  useEffect(() => {
    const fetchUserLoanInfo = async () => {
      try {
        const response = await axios.get(`http://localhost:8765/CARDSLOANS-SERVICE/api/v1/loans/view/userLoan?userLoanID=${userLoanId}`, { headers });
        setUserLoanInfo(response.data);
        setIsLoaded(true);
      } catch (error) {
        console.error('Error fetching user loan information:', error);
      }
    };
  
    const fetchLoanInfo = async () => {
      try {
        const response = await axios.get(`http://localhost:8765/CARDSLOANS-SERVICE/api/v1/loans/view/${userLoanInfo.loanID}`, { headers });
        setLoanInfo(response.data);
      } catch (error) {
        console.error('Error fetching loan information:', error);
      }
    };
  
    if (!isLoaded) {
      fetchUserLoanInfo();
    } else if (userLoanInfo.loanID) {
      fetchLoanInfo();
    }
  }, [isLoaded, userLoanId, userLoanInfo.loanID]);

  return (
    <div className='userloan'>
      <h2>User Loan Information</h2>
      <div className='loancard'>
        <p>Loan Amount: {userLoanInfo.loanAmount}</p>
        <p>Loan Status: {userLoanInfo.loanStatus ? 'Active' : 'Inactive'}</p>
        <p>Loan Start Date: {new Date(userLoanInfo.loanStartDate)?.toLocaleString()}</p>
        <p>Loan End Date: {userLoanInfo.loanEndDate ? new Date(userLoanInfo.loanEndDate)?.toLocaleString() : 'N/A'}</p>
        <p>Loan Type: {loanInfo.loanType}</p>
        <p>Annual Percentage Rate: {loanInfo.annualPercentageRate}</p>
        <p>Term Months: {loanInfo.termMonths}</p>
      </div>
    </div>
  );
};

export default ViewUserLoan;