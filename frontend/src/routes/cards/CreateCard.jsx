import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate, useLocation, useParams } from 'react-router-dom';
import Cookies from 'js-cookie';
import './CreateCard.css';

const CreateCard = () => {
  const [formData, setFormData] = useState({
    accountId: 0,
    pin: null,
    confirmpin: null,
    cardTypeId: 2,
    cardOffer: {
      cardOfferId: 0,
      cardOfferName: '',
      apr: 0,
      creditLimit: 0,
    },
  });
  
  const navigate = useNavigate();
  const location = useLocation();
  const selectedCardOffer = location.state?.creditOffer;
  const { accountId } = useParams();

  useEffect(() => {
    if (selectedCardOffer?.cardOfferId && selectedCardOffer?.cardOfferName && selectedCardOffer?.apr && selectedCardOffer?.creditLimit) {
      setFormData((prevData) => ({
        ...prevData,
        accountId: +accountId,
        cardOffer: {
          cardOfferId: selectedCardOffer.cardOfferId,
          cardOfferName: selectedCardOffer.cardOfferName,
          apr: selectedCardOffer.apr,
          creditLimit: selectedCardOffer.creditLimit,
        },
      }));
    }
  }, [accountId, selectedCardOffer]);

  const handleFormSubmit = async (e) => {
    e.preventDefault();
    try {
      console.log('FormData:', formData);
  
      const token = Cookies.get('token');
  
      const headers = {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
      };
  
      const payload = {
        accountID: formData.accountId,
        cardOffer: {
          cardOfferId: formData.cardOffer.cardOfferId,
          cardOfferName: formData.cardOffer.cardOfferName,
          apr: formData.cardOffer.apr,
          creditLimit: formData.cardOffer.creditLimit,
        },
        pin: parseInt(formData.pin, 10),
        cardTypeId: formData.cardTypeId,
      };
  
      const response = await axios.post('http://localhost:8765/CARDSLOANS-SERVICE/api/v1/cards/create', payload, { headers });
  
      const { cardNumber, cardOffer, pin, cardType, startDate, expirationDate } = response.data;
  
      navigate('/creditsuccess', {
        state: {
          cardNumber,
          cardOffer,
          pin,
          cardType,
          startDate,
          expirationDate,
        },
      });
    } catch (error) {
      console.error('Error creating card:', error);
    }
  };

  const isButtonDisabled = formData.pin !== formData.confirmpin;

  return (
    <div className='createCard'>
      <h2>Enter Your Pin Number for the Card</h2>
      <form onSubmit={handleFormSubmit}>
        <input type="hidden" value={formData.accountId} />

        <label>Please Enter Your PIN:</label>
        <input
          type="number"
          value={formData.pin}
          onChange={(e) => {
            const pinValue = e.target.value.replace(/\D/g, '');
            const truncatedPin = pinValue.slice(0, 5);
            setFormData({ ...formData, pin: truncatedPin });
          }}
        />

        <label>Repeat Your PIN:</label>
        <input
          type="number"
          value={formData.confirmpin}
          onChange={(e) => {
            const pinValue = e.target.value.replace(/\D/g, '');
            const truncatedPin = pinValue.slice(0, 5);
            setFormData({ ...formData, confirmpin: truncatedPin });
          }}
        />

        <div className='card-offer'>
          <h3>Selected Card Offer:</h3>
          <p>Name: {formData.cardOffer.cardOfferName}</p>
          <p>APR: {formData.cardOffer.apr}</p>
          <p>Credit Limit: {formData.cardOffer.creditLimit}</p>
        </div>

        <button className='golden-btn' type="submit" disabled={isButtonDisabled}>
          Create Card
        </button>
      </form>
    </div>
  );
};

export default CreateCard;