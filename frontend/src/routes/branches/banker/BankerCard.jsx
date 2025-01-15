import React, { useState } from 'react';

import PropTypes from 'prop-types';

import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';

import { CgProfile } from 'react-icons/cg';

import CreateBankerAppointment from '../modals/CreateBankerAppointment';

import './BankerCard.css';

const BankerCard = ({ banker }) => {
  const [showModal, setShowModal] = useState(false);

  const handleAppointmentButtonClick = () => {
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
  };

  return (
    <Card className="banker-card">
      <CardContent>
        <Typography variant="h5" component="div" className="banker-card-title">
          {banker.firstName} {banker.lastName} - {banker.jobTitle}
        </Typography>
        <div className="banker-profile-row">
          <div>
            <CgProfile className="profile-standin" />
          </div>
          <div className="banker-card-info-col">
            <Typography variant="body1" className="banker-card-text">
              <span className="card-detail-title">Phone Number: </span>
              {banker.phoneNumber}
            </Typography>
            <Typography variant="body1" className="banker-card-text">
              <span className="card-detail-title">Email: </span>
              {banker.email}
            </Typography>
            <div className="banker-appointment-modal-open-btn-container">
              <Button
                // variant="contained"
                className="banker-appointment-modal-open-bt"
                style={{
                  borderRadius: '3px',
                  backgroundColor: 'var(--contrast-darker)',
                  border: '1px solid var(--primary)',
                  margin: '20px',
                  color: 'var(--contrast-light)',
                }}
                onClick={handleAppointmentButtonClick}
              >
                Make Appointment
              </Button>
            </div>
            <CreateBankerAppointment
              show={showModal}
              onClose={handleCloseModal}
              selectedBanker={banker}
            />
          </div>
        </div>
      </CardContent>
    </Card>
  );
};

BankerCard.propTypes = {
  banker: PropTypes.shape({
    firstName: PropTypes.string.isRequired,
    lastName: PropTypes.string.isRequired,
    jobTitle: PropTypes.string.isRequired,
    phoneNumber: PropTypes.string.isRequired,
    email: PropTypes.string.isRequired
  }).isRequired,
};

export default BankerCard;
