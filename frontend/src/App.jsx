import React, { useEffect, useState } from 'react';
import Cookies from 'js-cookie';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import {
  Home,
  About,
  Careers,
  Help,
  SignupDetails,
  UserProfile,
  AccountCreation,
  AccountsView,
  Branches,
  Bankers,
  AccountDetailsView,
  Forbidden,
  LoanOffers,
  ViewUserLoan,
  ViewCard,
  CreditCardCreation,
  CreditOffers,
  CreateCard,
  SuccessCardPage,
  AllCards,
  Dashboard,
  UserSettings,
  PasswordResetRequest,
  PasswordResetForm
} from './routes/pages';
import * as Components from './components/components';
import { useGlobalState } from './utils/GlobalStateContext';
import './App.css';
import { getUserRole, getUserId, getUsername } from './utils/TokenUtils';
import useAuth from './utils/useAuth';
import useTokenValidation from './utils/validators/useTokenValidation';

const AuthWrapper = () => {
  useTokenValidation();
  return null;
};

export default function App() {
  const { setGlobalState } = useGlobalState();
  const [showModal, setShowModal] = useState(!Cookies.get('token'));
  const auth = useAuth();
  console.log(auth.isLoggedIn)

  useEffect(() => {
    const token = Cookies.get('token');

    if (token) {
      const role = getUserRole();
      const userId = getUserId();
      const username = getUsername();

      if (role && userId && username)
        setGlobalState({
          role,
          userId,
          username,
        });
    }
  }, [setGlobalState]);

  return (
    <Router>
      <AuthWrapper /> 
      <Components.Navigation showModal={showModal} setShowModal={setShowModal} />
      {auth.isLoggedIn && (
        <>
          <Components.Sidebar />
        </>
      )}

      <div className='App'>
        <Routes>
          <Route exact path='/' element={<Home />} />
          <Route path='/about' element={<About />} />
          <Route path='/help' element={<Help />} />
          <Route path='/careers' element={<Careers />} />
          <Route path='/signup-details' element={<SignupDetails />} />
          <Route path='/branches' element={<Branches />} />
          <Route path='/branches/:branchId/bankers' element={<Bankers />} />
          <Route path='/password-reset-request' element={<PasswordResetRequest />} />
          <Route path='/reset-password-form' element={<PasswordResetForm />} />
          {auth.isLoggedIn && (
            <>
              <Route path='/signup-details' element={<SignupDetails />} />
              <Route path='/user-profile' element={<UserProfile />} />
              <Route path='/dashboard' element={<Dashboard />} />
              <Route path='/accounts' element={<AccountsView />} />
              <Route path='/accounts/:accountId' element={<AccountDetailsView />} />
              <Route path='/accounts/open/:accountType' element={<AccountCreation />} />
              <Route path='/userloan/:userLoanId' element={<ViewUserLoan />} />
              <Route path='/viewcard/:cardID' element={<ViewCard />} />
              <Route path='/creditoffers/:accountId/:creditLimit' element={<CreditOffers />} />
              <Route path='/createCard/:accountId/:creditOfferId' element={<CreateCard />} />
              <Route path='/creditsuccess' element={<SuccessCardPage />} />
              <Route path='/cards' element={<AllCards />} />
              <Route path='/loans' element={<LoanOffers />} />
              <Route path='/signup/cards' element={<CreditCardCreation />} />
              <Route path='/user-settings' element={<UserSettings />} />
            </>
          )}
          <Route path='*' element={<Forbidden />} />
        </Routes>
        <Components.Footer />
      </div>
    </Router>
  );
}