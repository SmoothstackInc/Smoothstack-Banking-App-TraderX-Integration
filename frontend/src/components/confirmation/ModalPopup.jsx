import React from 'react';
import './ModalPopup.css';

const ModalPopup = ({ message, isOpen, onClose }) => {
  if (!isOpen) return null;

  return (
    <div className="confirmation-dialog-overlay" onClick={onClose}>
      <div className="confirmation-dialog" onClick={e => e.stopPropagation()}>
        <p>{message}</p>
        <button className="confirmation-dialog-button" onClick={onClose}>Close</button>
      </div>
    </div>
  );
};

export default ModalPopup;
