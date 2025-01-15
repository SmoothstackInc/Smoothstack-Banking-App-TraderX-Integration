import React from 'react';
import './CreditCard.css'; 

const CreditCard = ({ cardInfo }) => {
  const formatCardNumber = (number) => {
    if (!number) return '';
    const formattedNumber = number.toString().replace(/(\d{4})/g, '$1-');
    return formattedNumber.slice(0, -1); 
  };

  if (!cardInfo) {
    return <div className="credit-card">Card information not available</div>;
  }

  return (
    <div className="credit-card">
      <div className="credit-cardchip"></div>
      <div className="credit-cardinfo">
        <div className="credit-cardnumber">{formatCardNumber(cardInfo.cardNumber)}</div>
        <div className="credit-carddetails">
          <div className="credit-carddetail">
            <span className="credit-cardlabel">Card Offer</span>
            <span className="credit-cardvalue">{cardInfo.cardOffer || 'N/A'}</span>
          </div>
          <div className="credit-carddetail">
            <span className="credit-cardlabel">Expires</span>
            <span className="credit-cardvalue">{new Date(cardInfo.expirationDate)?.toLocaleDateString() || 'N/A'}</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CreditCard;