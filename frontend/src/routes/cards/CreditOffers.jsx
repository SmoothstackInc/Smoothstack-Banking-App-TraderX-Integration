import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate, useParams } from 'react-router-dom';
import Cookies from 'js-cookie';
import './CreditOffers.css';

const CreditOffers = () => {
  const [cardOffers, setCardOffers] = useState([]);
  const [selectedCardIndex, setSelectedCardIndex] = useState(0);
  const [filter, setFilter] = useState({
    type: 'creditLimit', 
    value: '',
  });
  const navigate = useNavigate();
  const { accountId, creditLimit } = useParams();

  useEffect(() => {
    const fetchCardOffers = async () => {
      try {
        const token = Cookies.get('token'); 

        const headers = {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        };

        const url = `http://localhost:8765/CARDSLOANS-SERVICE/api/v1/cards/creditoffers/limit?creditLimit=${creditLimit}`;

        const response = await axios.get(url, { headers });
        setCardOffers(response.data);
      } catch (error) {
        console.error('Error fetching card offers:', error);
      }
    };

    fetchCardOffers();
  }, [accountId, creditLimit]);

  const handleCardChange = (index, offer) => {
    setSelectedCardIndex(index);
    navigate(`/createCard/${accountId}/${offer.cardOfferId}`, { state: { creditOffer: offer } });
  };

  const handleFilterChange = (e) => {
    const { name, value } = e.target;
    setFilter((prevFilter) => ({ ...prevFilter, [name]: value }));
  };

  const filterCardOffers = () => {
    return cardOffers.filter((offer) => {
      const filterValue = parseFloat(filter.value);

      if (filter.type === 'creditLimit') {
        return isNaN(filterValue) || offer.creditLimit >= filterValue;
      } else if (filter.type === 'apr') {
        return isNaN(filterValue) || offer.apr <= filterValue;
      }


      return true;
    });
  };

  const renderCardList = () => {
    const filteredCardOffers = filterCardOffers();

    return filteredCardOffers.map((offer, index) => (
      <div
        key={offer.cardOfferId}
        className={`card-list-item ${index === selectedCardIndex ? 'selected' : ''}`}
        onClick={() => handleCardChange(index, offer)}
      >
        <strong>{offer.cardOfferName}</strong>
        <p>APR: {offer.apr}</p>
        <p>Credit Limit: {offer.creditLimit}</p>
      </div>
    ));
  };

  return (
    <div className="creditoffers">
      <div className='creditofferscontainer'>
      <h1>Here are your Credit Card offers!</h1>
  <div className='creditfilter'>
        <label>Filter Type:</label>
        <select name="type" value={filter.type} onChange={handleFilterChange}>
          <option value="creditLimit">Credit Limit</option>
          <option value="apr">APR</option>
          {/* Add more options for other filters */}
        </select>
      
      <div>
        <label>Filter Value:</label>
        <input type="text" name="value" value={filter.value} onChange={handleFilterChange} />
      </div>
      </div>
      </div>
      <div className="card-list">{renderCardList()}</div>
    </div>
  );
};

export default CreditOffers;