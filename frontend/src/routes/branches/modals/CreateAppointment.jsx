import React, { useState, useEffect } from 'react';

import PropTypes from 'prop-types';
import moment from 'moment';

import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  TextField,
  Typography,
} from '@mui/material';

import { getUserId } from '../../../utils/TokenUtils';
import {
  checkAvailability,
  createAppointment,
  fetchAvailableBankers,
  fetchBranches,
  fetchServiceTypes,
} from '../../../services/AppointmentService';
import AppointmentDatePicker from './AppointmentDatePicker';

import './CreateAppointment.css';

function CreateAppointment({ setOpenSnackbar }) {
  const [appointmentModalShow, setAppointmentModalShow] = useState(false);
  const [branches, setBranches] = useState([]);
  const [selectedDate, setSelectedDate] = useState(null);
  const [availableStartTimes, setAvailableStartTimes] = useState([]);
  const [selectedTime, setSelectedTime] = useState(null);
  const [availableBankers, setAvailableBankers] = useState([]);
  const [serviceTypes, setServiceTypes] = useState([]);
  const [isAppointmentCreated, setIsAppointmentCreated] = useState(false);

  const [appointmentData, setAppointmentData] = useState({
    userId: null,
    branchId: '',
    timeslot: '',
    bankerId: null,
    serviceId: null,
    description: '',
  });

  CreateAppointment.propTypes = {
    setOpenSnackbar: PropTypes.func.isRequired,
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
    const initializeData = async () => {
      const branchesData = await fetchBranches();
      setBranches(branchesData);

      const userIdFromToken = getUserId();
      if (userIdFromToken) {
        setAppointmentData((appointmentData) => ({
          ...appointmentData,
          userId: userIdFromToken,
        }));
      }
    };

    initializeData();
  }, []);

  const handleBranchChange = async (e) => {
    const branchId = e.target.value;
    setAppointmentData({ ...appointmentData, branchId });
  };

  const handleDateChange = async (selectedDate) => {
    const formattedDate = moment(selectedDate).format('YYYY-MM-DD');
    setSelectedDate(formattedDate);
  };

  useEffect(() => {
    const updateAvailability = async () => {
      if (appointmentData.branchId && selectedDate) {
        const startTimeAvailability = await checkAvailability(
          appointmentData.branchId,
          startTimes,
          selectedDate
        );
        setAvailableStartTimes(startTimeAvailability);
      }
    };

    updateAvailability();
  }, [appointmentData.branchId, selectedDate]);

  const renderStartTimes = () => {
    const selectableStartTimes = startTimes.filter(
      (startTime) => availableStartTimes[startTime]
    );

    return selectableStartTimes.map((startTime) => (
      <MenuItem key={startTime} value={startTime}>
        {startTime}
      </MenuItem>
    ));
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
    const updateAvailableBankers = async () => {
      if (appointmentData.branchId && selectedDate && selectedTime) {
        const formattedDateTime = moment(
          selectedDate + ' ' + selectedTime
        ).format('YYYY-MM-DDTHH:mm:ss');
        const availableBankersData = await fetchAvailableBankers(
          appointmentData.branchId,
          formattedDateTime
        );
        setAvailableBankers(availableBankersData);
      }
    };

    updateAvailableBankers();
  }, [appointmentData.branchId, selectedDate, selectedTime]);

  const renderAvailableBankers = () => {
    return availableBankers.map((banker) => (
      <MenuItem key={banker.bankerId} value={banker.bankerId}>
        {banker.firstName} {banker.lastName} - {banker.jobTitle}
      </MenuItem>
    ));
  };

  const handleBankerChange = (e) => {
    const bankerId = e.target.value;
    setAppointmentData({ ...appointmentData, bankerId });
  };

  useEffect(() => {
    const initializeServiceTypes = async () => {
      const serviceTypesData = await fetchServiceTypes();
      setServiceTypes(serviceTypesData);
    };

    initializeServiceTypes();
  }, []);

  const renderServiceTypes = () => {
    return serviceTypes.map((serviceType) => (
      <MenuItem key={serviceType.serviceId} value={serviceType.serviceId}>
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
          'YYYY-MM-DDTHH:mm:ss'
        );
        const updatedAppointmentData = { ...appointmentData, timeslot };
        await createAppointment(updatedAppointmentData);
        setIsAppointmentCreated(true);
        setAppointmentModalShow(false);
        setOpenSnackbar(true);
      } else {
        console.error('Incomplete appointment information');
      }
    } catch (error) {
      console.error('Error creating appointment:', error);
    }
  };

  const handleClose = () => {
    setIsAppointmentCreated(false);
    setAppointmentModalShow(false);
  };

  return (
    <>
      <div className="appointment-modal-container">
        <div className="appointment-modal-open-btn-container">
          <Button
            onClick={() => setAppointmentModalShow(true)}
            style={{
              borderRadius: '3px',
              backgroundColor: 'var(--primary)',
              border: '1px solid var(--card-background)',
              color: 'var(--card-background)',
              fontWeight: 'bold',
            }}
            className="appointment-modal-open-btn"
          >
            Create Appointment
          </Button>
        </div>
        <Dialog
          open={appointmentModalShow}
          onClose={() => setAppointmentModalShow(false)}
          PaperProps={{
            component: 'form',
            onSubmit: handleSubmit,
          }}
          aria-labelledby="appointment-modal-title"
          className="appointment-modal"
        >
          <div className="appointment-modal-main">
            <DialogTitle variant="h6" id="appointment-modal-title">
              Create Appointment
            </DialogTitle>
            <hr />
            <DialogContent className="appointment-modal-dialog-content">
              {isAppointmentCreated ? (
                <div className="appointment-success-message">
                  Appointment successfully created!
                </div>
              ) : (
                <div
                  style={{
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                    justifyContent: 'center',
                    color: 'ivory',
                  }}
                >
                  <DialogContentText className="appointment-modal-dialog-content-text">
                    Welcome to the appointment creator! Please fill in the
                    fields and press submit.
                  </DialogContentText>
                  <FormControl fullWidth className="appointment-form-element">
                    <InputLabel>Select a Branch</InputLabel>
                    <Select
                      name="branchId"
                      value={appointmentData.branchId}
                      onChange={handleBranchChange}
                    >
                      <MenuItem value="" disabled></MenuItem>
                      {branches.map((branch) => (
                        <MenuItem key={branch.branchId} value={branch.branchId}>
                          {branch.branchName}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>

                  <FormControl
                    fullWidth
                    className="appointment-modal-form-control-datepicker"
                  >
                    <AppointmentDatePicker onChange={handleDateChange} />
                  </FormControl>

                  <FormControl
                    fullWidth
                    style={{
                      marginBottom: '1vh',
                    }}
                    className="appointment-form-element"
                  >
                    <InputLabel className="appointment-modal-select-label">Select a Start Time</InputLabel>
                    <Select
                      name="startTime"
                      value={selectedTime || ''}
                      onChange={(e) => handleTimeslotChange(e)}
                    >
                      <MenuItem value="" disabled>
                        Select a Start Time
                      </MenuItem>
                      {renderStartTimes()}
                    </Select>
                  </FormControl>

                  <FormControl
                    fullWidth
                    style={{
                      marginBottom: '1vh',
                    }}
                    className="appointment-form-element"
                  >
                    <InputLabel>Select a Banker</InputLabel>
                    <Select
                      name="bankerId"
                      value={appointmentData.bankerId || ''}
                      onChange={handleBankerChange}
                    >
                      <MenuItem value="" disabled>
                        Select a Banker
                      </MenuItem>
                      {availableBankers.length > 0 ? (
                        renderAvailableBankers()
                      ) : (
                        <Typography>
                          No available bankers at the selected timeslot.
                        </Typography>
                      )}
                    </Select>
                  </FormControl>

                  <FormControl
                    fullWidth
                    style={{
                      marginBottom: '1vh',
                    }}
                    className="appointment-form-element"
                  >
                    <InputLabel className="appointment-modal-select-field">
                      Select a Service Type
                    </InputLabel>
                    <Select
                      name="serviceType"
                      value={appointmentData.serviceId || ''}
                      onChange={(e) => handleServiceTypeChange(e)}
                    >
                      <MenuItem value="" disabled>
                        Select a Service Type
                      </MenuItem>
                      {renderServiceTypes()}
                    </Select>
                  </FormControl>

                  <TextField
                    label="Describe the purpose of your appointment."
                    multiline
                    rows={3}
                    value={appointmentData.description}
                    onChange={handleDescriptionChange}
                    fullWidth
                    style={{
                      marginBottom: '1vh',
                    }}
                    className="appointment-form-element"
                  />
                </div>
              )}
            </DialogContent>
            <DialogActions className="create-appt-actions-container">
              <Button
                onClick={handleClose}
                style={{
                  borderRadius: '3px',
                  backgroundColor: 'var(--contrast-darker',
                  border: '1px solid var(--primary-dark)',
                  margin: '20px',
                  color: 'var(--contrast-light)',
                }}
                className="create-appt-cancel-btn"
              >
                Cancel
              </Button>
              <Button
                type="submit"
                variant="contained"
                style={{
                  borderRadius: '3px',
                  backgroundColor: 'var(--contrast-darker)',
                  border: '1px solid var(--primary-light)',
                  margin: '20px',
                  color: 'var(--contrast-light)',
                }}
                className="create-appt-submit-btn"
              >
                Submit
              </Button>
            </DialogActions>
          </div>
        </Dialog>
      </div>
    </>
  );
}

export default CreateAppointment;
