import React from 'react';
import Button from '@mui/material/Button';
import './secondary-btn.css';

const SecondaryBtnComponent = ({ buttonText = "Click", ...props }) => {
  return (
    <Button variant="outlined" className="secondary-btn" {...props}>
      {buttonText}
    </Button>
  );
}

export default SecondaryBtnComponent;
