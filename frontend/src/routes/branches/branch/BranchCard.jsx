import React, { useState } from 'react';
import PropTypes from 'prop-types';
import { useNavigate } from 'react-router-dom';
import { Card, Typography, Button, CardContent } from '@mui/material';
import { RoomOutlined as RoomOutlinedIcon } from '@mui/icons-material';
import BranchMap from '../map/BranchMap';
import './BranchCard.css';

const BranchCard = ({ branch }) => {
  const navigate = useNavigate();
  const [mapLoaded, setMapLoaded] = useState(true);

  const handleShowBankers = (branchId) => {
    navigate(`/branches/${branchId}/bankers`);
  };

  const handleMapLoadSuccess = () => {
    setMapLoaded(true);
  };

  const handleMapLoadError = () => {
    setMapLoaded(false);
  };

  return (
    <Card
      style={{
        backgroundColor: 'var(--card-background)',
        color: 'var(--contrast-light)',
        boxShadow: 'inset 0 0 2px 0 var(--primary-light)',
      }}
      className="branch-card">
      <div className="branch-card-title-container">
        <Typography
          variant="h5"
          style={{
            padding: '1rem 0 0 0',
          }}
          className="branch-card-title"
        >
          {branch.branchName}
        </Typography>
        <hr />
      </div>
      <CardContent className="branch-card-body">
        {mapLoaded ? (
          <BranchMap
            lat={branch.lat}
            lng={branch.lng}
            onLoad={handleMapLoadSuccess}
            onError={handleMapLoadError}
          />
        ) : (
          <RoomOutlinedIcon className="map-standin" />
        )}
        <Typography variant="body1" className="branch-card-text">
          <span className="branch-card-detail">
            <span className="branch-card-detail-title">Manager</span>:{' '}
            {branch.branchManager}
          </span>
          <span className="branch-card-detail">
            <span className="branch-card-detail-title">Phone Number</span>:{' '}
            {branch.phoneNumber}
          </span>
          <span className="branch-card-detail">
            <span className="branch-card-detail-title">Email</span>:{' '}
            {branch.email}
          </span>
        </Typography>
        <Typography variant="body1" className="branch-card-text">
          {/* specialties, availability, and ratings */}
          {/* {branch.description} */}
          <span className="branch-card-detail">
            <span className="branch-card-detail-title">Hours of Operation</span>
            : 8:00AM - 5:00PM, Monday - Friday
          </span>
          <span className="branch-card-detail">
            {branch.address1} {branch.city}, {branch.state} {branch.postalCode}
          </span>
        </Typography>

        <div className="branch-primary-btn-container">
          <Button
            onClick={() => handleShowBankers(branch.branchId)}
            style={{
              borderRadius: '3px',
              backgroundColor: 'var(--contrast-darker)',
              border: '1px solid var(--primary)',
              margin: '20px',
              color: 'var(--contrast-light)',
            }}
            className="branch-primary-btn"
          >
            Meet Our Team
          </Button>
        </div>
      </CardContent>
    </Card>
  );
};

BranchCard.propTypes = {
  branch: PropTypes.shape({
    branchId: PropTypes.number.isRequired,
    branchName: PropTypes.string.isRequired,
    branchManager: PropTypes.number.isRequired,
    phoneNumber: PropTypes.string.isRequired,
    email: PropTypes.string.isRequired,
    address1: PropTypes.string.isRequired,
    city: PropTypes.string.isRequired,
    state: PropTypes.string.isRequired,
    postalCode: PropTypes.string.isRequired,
    lat: PropTypes.number.isRequired,
    lng: PropTypes.number.isRequired,
  }).isRequired,
};

export default BranchCard;
