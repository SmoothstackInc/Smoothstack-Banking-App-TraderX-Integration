import React from 'react';
import { useLocation } from 'react-router-dom';
import CreditCard from '../../components/CardComponents/CreditCard';
import './ViewCard.css';

const SuccessCardPage = () => {
  const location = useLocation();
  const {
    cardNumber,
    cardOffer,
    pin,
    cardType,
    startDate,
    expirationDate,
  } = location.state;

  const formatDate = (date) => {
    return new Date(date).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  };

  const cardInfo = {
    cardNumber,
    cardOffer,
    pin,
    cardType,
    startDate,
    expirationDate,
  };

  return (
    <div className='card-view'>
      <h2>Card Created Successfully</h2>
      <CreditCard cardInfo={cardInfo} />
      <div className='card-info'>
        <p>Card Offer: {cardInfo.cardOffer}</p>
        <p>PIN: {cardInfo.pin}</p>
        <p>Card Type: {cardInfo.cardType}</p>
        <p>Start Date: {formatDate(cardInfo.startDate)}</p>
        <p>Expiration Date: {formatDate(cardInfo.expirationDate)}</p>
      </div>
    </div>
  );
};

export default SuccessCardPage;