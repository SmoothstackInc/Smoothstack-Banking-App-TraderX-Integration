import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate, useParams } from 'react-router-dom';
import CreditCard from '../../components/CardComponents/CreditCard';
import Cookies from 'js-cookie';
import './ViewCard.css';

const ViewCard = () => {
  const navigate = useNavigate();
  const { cardID } = useParams();

  const [cardInfo, setCardInfo] = useState({});
  const [transactions, setTransactions] = useState([]);
  const [isLoaded, setIsLoaded] = useState(false);

  const token = Cookies.get('token'); 
  const headers = {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${token}`,
  };

  useEffect(() => {
    const fetchCardInfo = async () => {
      try {
        const response = await axios.get(`http://localhost:8765/CARDSLOANS-SERVICE/api/v1/cards/userCard?cardID=${cardID}`, { headers });
        setCardInfo(response.data);
        setIsLoaded(true);
        const accountID = response.data.accountID;
        const transactionsResponse = await axios.get(`http://localhost:8765/ACCOUNTS-SERVICE/api/v1/transactions/by-account/${accountID}`, { headers });
        setTransactions(transactionsResponse.data.data);
      } catch (error) {
        console.error('Error fetching card information:', error);
      }
    };

    if (!isLoaded) {
      fetchCardInfo();
    }
  }, [cardID, isLoaded, headers]);

  const handleDeactivate = async () => {
    try {
      await axios.delete(`http://localhost:8765/CARDSLOANS-SERVICE/api/v1/cards/delete/${cardID}`, { headers });
      navigate('/cards');
    } catch (error) {
      console.error('Error deactivating card:', error);
    }
  };

  const renderTransactionsTable = () => {
    const maxTransactionsToShow = 10;
    const displayedTransactions = transactions.slice(0, maxTransactionsToShow);
  
    if (displayedTransactions.length === 0) {
      return <p>No transactions available.</p>;
    }
  
    return (
      <div className="transactions-table">
        <h3>Transaction History</h3>
        <table>
          <thead>
            <tr>
              <th>Date</th>
              <th>Description</th>
              <th>Amount</th>
            </tr>
          </thead>
          <tbody>
            {displayedTransactions.map((transaction) => (
              <tr key={transaction.transactionId}>
                <td>{new Date(transaction.dateTime).toLocaleDateString()}</td>
                <td>{transaction.description}</td>
                <td>{transaction.amount}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    );
  };

  return (
    <div className="card-view">
      <h2>Your Card Info</h2>
      <CreditCard cardInfo={cardInfo} />
      <div className="card-info">
        <p>Card Offer: {cardInfo.cardOffer}</p>
        <p>PIN: {cardInfo.pin}</p>
        <p>Card Type: {cardInfo.cardType}</p>
        <p>Start Date: {new Date(cardInfo.startDate)?.toLocaleDateString()}</p>
        <p>Expiration Date: {new Date(cardInfo.expirationDate)?.toLocaleDateString()}</p>

        <button className="deactivate-button" onClick={handleDeactivate}>
         Freeze Card
        </button>
      </div>
      
      {renderTransactionsTable()}
    </div>
  );
};

export default ViewCard;