import React, { useState } from 'react';
// import BranchDisplay from './BranchDisplay';
import BranchTable from './BranchTable';
import CreateAppointment from '../modals/CreateAppointment';
import AppointmentSnackbar from '../modals/AppointmentSnackbar';
import './Branch.css';

function Branch() {
  const [openSnackbar, setOpenSnackbar] = useState(false);

  const handleOpenSnackbar = () => {
    setOpenSnackbar(true);
  }

  return (
    <div className="branch-page-container">
      <div className="spacer"></div>

      <div id="branch" className="branch">
        {/* <BranchDisplay /> */}
        <BranchTable />
        <CreateAppointment setOpenSnackbar={handleOpenSnackbar} />
        <AppointmentSnackbar open={openSnackbar} setOpen={setOpenSnackbar} />
      </div>
    </div>
  );
}

export default Branch;
