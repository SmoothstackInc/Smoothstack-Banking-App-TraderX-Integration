import React from 'react';
import Button from '@mui/material/Button';
import './primary-btn.css';

const PrimaryBtnComponent = ({onClick, buttonText = "Click" }) => {
  return (
    <Button onClick={onClick} variant="contained" className="primary-btn">
      {buttonText}
    </Button>
  );
}

export default PrimaryBtnComponent;
