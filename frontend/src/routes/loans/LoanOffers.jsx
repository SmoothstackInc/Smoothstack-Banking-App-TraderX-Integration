import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate, useParams } from 'react-router-dom';
import Cookies from 'js-cookie';
import './LoanOffers.css';

const LoanOffers = () => {
  const [loanOffers, setLoanOffers] = useState([]);
  const [selectedLoanIndex, setSelectedLoanIndex] = useState(null);
  const [showConfirmationModal, setShowConfirmationModal] = useState(false);
  const [loanAmount, setLoanAmount] = useState('');
  const [selectedLoanType, setSelectedLoanType] = useState('All'); 
  const navigate = useNavigate();
  const { accountId } = useParams();

  useEffect(() => {
    const fetchLoanOffers = async () => {
      try {
        const token = Cookies.get('token');

        const headers = {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        };

        const apiUrl = selectedLoanType === 'All'
          ? 'http://localhost:8765/CARDSLOANS-SERVICE/api/v1/loans/view'
          : `http://localhost:8765/CARDSLOANS-SERVICE/api/v1/loans/viewLoans?loanTypeName=${selectedLoanType}`;

        const response = await axios.get(apiUrl, { headers });
        setLoanOffers(response.data);
      } catch (error) {
        console.error('Error fetching loan offers:', error);
      }
    };

    fetchLoanOffers();
  }, [accountId, selectedLoanType]);

  const handleLoanChange = (index, offer) => {
    setSelectedLoanIndex(index);
    setShowConfirmationModal(true);
  };

  const handleLoanTypeChange = (event) => {
    setSelectedLoanType(event.target.value);
  };

  const handleConfirmApply = async () => {
    try {
      const selectedLoan = loanOffers[selectedLoanIndex];
      const token = Cookies.get('token');

      const headers = {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
      };

      const enteredLoanAmount = parseFloat(loanAmount);

      if (enteredLoanAmount < selectedLoan.minAmount || enteredLoanAmount > selectedLoan.maxAmount) {
        window.alert('Error: Loan amount must be within the specified range.');
        return;
      }

      const accountId = selectedLoan.accountId;

      const accountCreationPayload = {
        userId: 184,
        accountType: 'CREDIT',
        initialBalance: enteredLoanAmount,
      };

      const accountCreationResponse = await axios.post('http://localhost:8765/ACCOUNTS-SERVICE/api/v1/accounts', accountCreationPayload, { headers });

      const createdAccountId = accountCreationResponse.data.data.accountId;
      console.log('Loan account creation successful with ID:', createdAccountId);

      const loanPayload = {
        loanID: selectedLoan.loanID,
        accountID: createdAccountId,
        loanAmount: enteredLoanAmount,
      };

      const loanResponse = await axios.post('http://localhost:8765/CARDSLOANS-SERVICE/api/v1/loans/apply', loanPayload, { headers });

      console.log('Loan application successful:', loanResponse.data);

      const userLoanId = loanResponse.data.userLoanID;
      navigate(`/userloan/${userLoanId}`);
      setShowConfirmationModal(false);
    } catch (error) {
      console.error('Error applying for loan or creating account:', error);
    }
  };

  const renderLoanList = () => {
    return loanOffers.map((offer, index) => (
      <div
        key={offer.loanID}
        className={`loan-list-item ${index === selectedLoanIndex ? 'selected' : ''}`}
        onClick={() => handleLoanChange(index, offer)}
      >
        <strong>{offer.loanType}</strong>
        <p>APR: {offer.annualPercentageRate}</p>
        <p>Term: {offer.termMonths} months</p>
        <p>Amount: {offer.minAmount} - {offer.maxAmount}</p>
      </div>
    ));
  };

  return (
    <div className="loanoffer">
      <div className='top'>
        <h1>Here are your Loan Offers!</h1>
        <div className="loanfilter">
        <label>Select Loan Type:</label>
        <select value={selectedLoanType} onChange={handleLoanTypeChange}>
          <option value="All">All Offers</option>
          <option value="Mortgage">Mortgage</option>
          <option value="Personal Loans">Personal Loans</option>
          <option value="Auto Loans">Auto Loans</option>
          <option value="Student Loans">Student Loans</option>
        </select>
      </div>
      </div>

      <div className="loan-list">{renderLoanList()}</div>

      {showConfirmationModal && (
        <div className="loan-offers-modal-overlay">
          <div className="loan-offers-modal">
            <span className="close-btn" onClick={() => setShowConfirmationModal(false)}>
              &times;
            </span>
            <p>Are you sure you want to apply for this loan?</p>
            <label>Enter loan amount:</label>
            <input type="number" value={loanAmount} onChange={(e) => setLoanAmount(e.target.value)} />
            <button onClick={handleConfirmApply}>Confirm</button>
            <button onClick={() => setShowConfirmationModal(false)}>Cancel</button>
          </div>
        </div>
      )}
    </div>
  );
};

export default LoanOffers;
