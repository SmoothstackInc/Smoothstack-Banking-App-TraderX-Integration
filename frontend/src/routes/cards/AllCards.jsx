import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Cookies from 'js-cookie';
import './AllCards.css';

const AllCards = () => {
  const [cardsData, setCardsData] = useState([]);
  const [isLoading, setIsLoading] = useState(true); // Track loading state
  const jwtToken = Cookies.get('token');
  const navigate = useNavigate();

  useEffect(() => {
    const fetchCardsData = async () => {
      try {
        const response = await fetch('http://localhost:8765/CARDSLOANS-SERVICE/api/v1/cards/user', {
          headers: {
            Authorization: `Bearer ${jwtToken}`,
          },
        });
        const data = await response.json();
        setCardsData(data);
        setIsLoading(false);
      } catch (error) {
        console.error('Error fetching cards data:', error);
        setIsLoading(false);
      }
    };

    fetchCardsData();
  }, [jwtToken]);

  const handleCardClick = (cardID) => {
    navigate(`/viewcard/${cardID}`);
  };

  const navigateToCreditCardCreation = () => {
    navigate('/signup/cards');
  };

  return (
    <div className='all-cards'>
      <div className='first-header'>
        <h1>Here are all your Credit Cards!</h1>
        <button className='golden-btn' onClick={navigateToCreditCardCreation}>
          Create Card
        </button>
      </div>
      {isLoading ? (
        <p>Loading...</p>
      ) : (
        <table className='cards-table'>
          <thead>
            <tr>
              <th>Card Number</th>
              <th>Card Offer</th>
              <th>Card Type</th>
              <th>Start Date</th>
              <th>Expiration Date</th>
            </tr>
          </thead>
          <tbody>
            {cardsData.length === 0 ? (
              <tr>
                <td colSpan='5'>No Cards Created, please create a card</td>
              </tr>
            ) : (
              cardsData.map((card) => (
                <tr key={card.cardID} onClick={() => handleCardClick(card.cardID)}>
                  <td>{`**** **** **** ${card.cardNumber.toString().slice(-4)}`}</td>
                  <td>{card.cardOffer}</td>
                  <td>{card.cardType}</td>
                  <td>{new Date(card.startDate).toLocaleDateString()}</td>
                  <td>{new Date(card.expirationDate).toLocaleDateString()}</td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default AllCards;
