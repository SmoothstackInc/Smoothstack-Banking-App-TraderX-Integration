import React, { useState, useEffect } from 'react';

import PropTypes from 'prop-types';
import moment from 'moment';

import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import FormControl from '@mui/material/FormControl';
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import Select from '@mui/material/Select';
import TextField from '@mui/material/TextField';

import { getUserId } from '../../../utils/TokenUtils';
import {
  checkBankerAvailability,
  createAppointment,
  fetchServiceTypes,
} from '../../../services/AppointmentService';
import AppointmentDatePicker from './AppointmentDatePicker';

import './CreateBankerAppointment.css';

function CreateBankerAppointment({ show, onClose, selectedBanker }) {
  const [selectedDate, setSelectedDate] = useState(null);
  const [availableStartTimes, setAvailableStartTimes] = useState([]);
  const [selectedTime, setSelectedTime] = useState(null);
  const [serviceTypes, setServiceTypes] = useState([]);
  const [isAppointmentCreated, setIsAppointmentCreated] = useState(false);

  const [appointmentData, setAppointmentData] = useState({
    userId: null,
    branchId: null,
    timeslot: '',
    bankerId: null,
    serviceId: null,
    description: '',
  });

  CreateBankerAppointment.propTypes = {
    show: PropTypes.bool.isRequired,
    onClose: PropTypes.func.isRequired,
    selectedBanker: PropTypes.shape({
      bankerId: PropTypes.number.isRequired,
      branchId: PropTypes.number.isRequired,
    }).isRequired,
  };

  const startTimes = [
    '08:00:00',
    '09:00:00',
    '10:00:00',
    '11:00:00',
    '13:00:00',
    '14:00:00',
    '15:00:00',
    '16:00:00',
  ];

  useEffect(() => {
    const userIdFromToken = getUserId();
    if (selectedBanker && userIdFromToken) {
      setAppointmentData((appointmentData) => ({
        ...appointmentData,
        userId: userIdFromToken,
        bankerId: selectedBanker.bankerId,
        branchId: selectedBanker.branchId,
      }));
    }
  }, [selectedBanker]);

  const handleDateChange = async (selectedDate) => {
    const formattedDate = selectedDate
      ? moment(selectedDate).format('YYYY-MM-DD')
      : null;
    setSelectedDate(formattedDate);
  };

  useEffect(() => {
    const checkAvailability = async () => {
      try {
        if (appointmentData.bankerId && selectedDate) {
          const bankerId = appointmentData.bankerId;

          const startTimeAvailability = {};

          for (const startTime of startTimes) {
            const formattedDateTime = moment(
              selectedDate + ' ' + startTime
            ).format('YYYY-MM-DDTHH:mm:ss');

            const appointmentsCount = await checkBankerAvailability(
              bankerId,
              formattedDateTime
            );

            startTimeAvailability[startTime] = appointmentsCount.length === 0;
          }

          setAvailableStartTimes(startTimeAvailability);
        }
      } catch (error) {
        console.error('Error checking availability:', error);
      }
    };

    checkAvailability();
  }, [appointmentData.bankerId, selectedDate]);

  const renderStartTimes = () => {
    const selectableStartTimes = startTimes.filter(
      (startTime) => availableStartTimes[startTime]
    );

    return [
      <MenuItem key="" value="" disabled>
        Select a Start Time
      </MenuItem>,
      ...selectableStartTimes.map((startTime) => (
        <MenuItem
          key={startTime}
          value={startTime}
          className="create-banker-start-time"
        >
          {startTime}
        </MenuItem>
      )),
    ];
  };

  const handleTimeslotChange = (e) => {
    const selectedTime = e.target.value;
    setSelectedTime(selectedTime);

    const formattedDateTime = moment(selectedDate + ' ' + selectedTime).format(
      'YYYY-MM-DDTHH:mm:ss'
    );
    setAppointmentData({ ...appointmentData, timeslot: formattedDateTime });
  };

  useEffect(() => {
    const fetchServiceTypesData = async () => {
      try {
        const serviceTypesData = await fetchServiceTypes();
        setServiceTypes(serviceTypesData);
      } catch (error) {
        console.error('Error fetching service types:', error);
        setServiceTypes([]);
      }
    };

    fetchServiceTypesData();
  }, []);

  const renderServiceTypes = () => {
    return serviceTypes.map((serviceType) => (
      <MenuItem
        key={serviceType.serviceId}
        value={serviceType.serviceId}
        className="create-banker-service-type"
      >
        {serviceType.serviceTypeName}
      </MenuItem>
    ));
  };

  const handleServiceTypeChange = (e) => {
    const serviceId = e.target.value;
    setAppointmentData({ ...appointmentData, serviceId });
  };

  const handleDescriptionChange = (e) => {
    const description = e.target.value;
    setAppointmentData({ ...appointmentData, description });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (
        appointmentData.branchId &&
        appointmentData.bankerId &&
        appointmentData.userId &&
        appointmentData.timeslot
      ) {
        const timeslot = moment(selectedDate + ' ' + selectedTime).format(
          'YYYY-MM-DD HH:mm:ss'
        );

        setAppointmentData({ ...appointmentData, timeslot });
        const createResponse = await createAppointment(appointmentData);

        console.log('Appointment created:', createResponse);
        setIsAppointmentCreated(true);
      } else {
        console.error('Incomplete appointment information');
      }
    } catch (error) {
      console.error('Error creating appointment:', error);
    }
  };

  const handleClose = () => {
    setIsAppointmentCreated(false);
    onClose();
  };

  return (
    <>
      <Dialog
        open={show}
        onClose={() => setIsAppointmentCreated(false)}
        PaperProps={{
          component: 'form',
          onSubmit: handleSubmit,
        }}
        aria-labelledby="banker-appointment-modal-title"
        className="banker-appointment-modal"
      >
        <div className="banker-appointment-modal-main">
          <DialogTitle variant="h6" id="banker-appointment-modal-title">
            Create Appointment
          </DialogTitle>
          <DialogContent className="banker-appointment-modal-dialog-content">
            {isAppointmentCreated ? (
              <div className="appointment-success-message">
                Appointment successfully created!
              </div>
            ) : (
              <div>
                <DialogContentText className="banker-appointment-modal-dialog-content-text">
                  Welcome to the appointment creator! Please fill in the fields
                  and press submit.
                </DialogContentText>
                <FormControl
                  fullWidth
                  className="banker-appointment-modal-form-control-datepicker"
                >
                  <AppointmentDatePicker onChange={handleDateChange} />
                </FormControl>

                <FormControl
                  fullWidth
                  className="banker-appointment-modal-form-control banker-appointment-modal-form-control-light"
                >
                  <InputLabel id="start-time-select-label">
                    Select a Start Time
                  </InputLabel>
                  <Select
                    labelId="start-time-select-label"
                    value={selectedTime || ''}
                    onChange={(e) => handleTimeslotChange(e)}
                  >
                    {renderStartTimes()}
                  </Select>
                </FormControl>

                <FormControl
                  fullWidth
                  className="banker-appointment-modal-form-control banker-appointment-modal-form-control-light"
                >
                  <InputLabel id="service-type-select-label">
                    Select a Service Type
                  </InputLabel>
                  <Select
                    labelId="service-type-select-label"
                    value={appointmentData.serviceId || ''}
                    onChange={(e) => handleServiceTypeChange(e)}
                    className="service-type-select"
                  >
                    {renderServiceTypes()}
                  </Select>
                </FormControl>

                <TextField
                  fullWidth
                  label="Describe the purpose of your appointment"
                  multiline
                  rows={3}
                  value={appointmentData.description}
                  onChange={handleDescriptionChange}
                  className="banker-appointment-modal-form-control banker-appointment-modal-form-control-light"
                />
              </div>
            )}
          </DialogContent>
          <DialogActions className="create-banker-actions-container">
            <Button onClick={handleClose} className="create-banker-cancel-btn">
              Cancel
            </Button>
            <Button
              type="submit"
              variant="contained"
              className="create-banker-submit-btn"
            >
              Submit
            </Button>
          </DialogActions>
        </div>
      </Dialog>
    </>
  );
}

export default CreateBankerAppointment;
