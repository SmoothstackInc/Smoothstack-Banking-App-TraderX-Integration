import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import Cookies from 'js-cookie';
import './CreditCardCreation.css';

const CreditCardCreation = () => {
  const [annualIncome, setAnnualIncome] = useState('');
  const [creditScore, setCreditScore] = useState('');
  const [creditLimit, setCreditLimit] = useState(null);
  const navigate = useNavigate();

  const handleCalculateCreditLimit = async () => {
    const calculatedCreditLimit = (parseInt(annualIncome) * parseInt(creditScore)) / 10000;

    setCreditLimit(calculatedCreditLimit);

    const userId = 1322;
    const accountType = 'Credit';
    const initialBalance = calculatedCreditLimit;

    const requestData = {
      userId,
      accountType,
      initialBalance,
    };

    const authToken = Cookies.get('token');

    try {
      const response = await axios.post('/api/v1/accounts', requestData, {
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${authToken}`,
        },
      });

      const accountId = response.data.data.accountId;
      const intCalculatedCreditLimit = parseInt(calculatedCreditLimit, 10);

      navigate(`/creditoffers/${accountId}/${intCalculatedCreditLimit}`);
    } catch (error) {
      console.error('Error creating account:', error);
    }
  };

  return (
    <div className='creation'>
      <div className='card-image'>
        <h2 className='card-heading'>
          <small>
            Thank you for choosing SecureSentinel Bank for your Credit Card needs! Please enter some further information
            so we can show you available card offers!
          </small>
        </h2>
      </div>
      <form className='card-form'>
        <div className='input-group'>
          <label className='input-label'>Please Enter Your Annual Income:</label>
          <input
            type='number'
            className='input-field'
            value={annualIncome}
            onChange={(e) => setAnnualIncome(e.target.value)}
          />
        </div>
        <div className='input-group'>
          <label className='input-label'>Please Enter Your Credit Score:</label>
          <input
            type='number'
            className='input-field'
            value={creditScore}
            onChange={(e) => setCreditScore(e.target.value)}
          />
        </div>
      </form>
      <button className='golden-btn' onClick={handleCalculateCreditLimit}>
        Show me my Offers
      </button>

      {creditLimit !== null && (
        <div>
          <h3>Calculated Credit Limit:</h3>
          <p>{creditLimit}</p>
        </div>
      )}
    </div>
  );
};

export default CreditCardCreation;
