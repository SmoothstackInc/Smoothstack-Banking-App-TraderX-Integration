import React, { useState } from 'react';
import PropTypes from 'prop-types';

import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { TextField, InputLabel } from '@mui/material';

import { addDays, isWeekend } from 'date-fns';

export default function AppointmentDatePicker({ onChange }) {
  const [selectedDate, setselectedDate] = useState(null);
  const [isPlaceholderVisible, setIsPlaceholderVisible] = useState(true);

  AppointmentDatePicker.propTypes = {
    onChange: PropTypes.func.isRequired,
  };

  function handleDateChange(date) {
    setselectedDate(date);
    setIsPlaceholderVisible(false);
    onChange(date);
  }

  const today = new Date();
  const endOfWeek = addDays(today, 6 - today.getDay());
  const dateRange = {
    start: today,
    end: endOfWeek,
  };

  const disableWeekends = (date) => {
    return isWeekend(date);
  };

  const renderInput = (props) => (
    <TextField
      {...props}
      placeholder={isPlaceholderVisible ? 'Select a Date' : null}
      onClick={() => setIsPlaceholderVisible(false)}
    />
  );

  return (
    <LocalizationProvider dateAdapter={AdapterDayjs}>
      <div className="banker-appointment-modal-dialog-content-container date-picker">
        {/* <InputLabel id="date-picker-label">Select a Date</InputLabel> */}
        <DatePicker
          value={selectedDate}
          onChange={handleDateChange}
          textField={renderInput}
          // textField={(props) => <TextField {...props} />}
          dateRange={dateRange}
          shouldDisableDate={disableWeekends}
          className="appointment-date-picker"
          // label="Select a Date"
        />
      </div>
    </LocalizationProvider>
  );
}
