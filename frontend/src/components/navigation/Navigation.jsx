import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faBars } from '@fortawesome/free-solid-svg-icons';
import SignUpModal from '../signupmodal/SignUpModal';
import SignInModal from '../signinmodal/SignInModal';
import { signOut } from '../../utils/Session';
import { useGlobalState } from '../../utils/GlobalStateContext';
import useAuth from '../../utils/useAuth';
import { useLocation } from 'react-router-dom';

const Navigation = ({ showModal, setShowModal }) => {
  const [isMenuVisible, setIsMenuVisible] = useState(false);
  const navigate = useNavigate();
  const { setGlobalState } = useGlobalState();
  const [isSigningIn, setIsSigningIn] = useState(true);
  const auth = useAuth();
  const location = useLocation();

  const isPasswordResetRoute = location.pathname === '/password-reset-request' || location.pathname === '/reset-password-form';

  const toggleMenu = () => {
    setIsMenuVisible(!isMenuVisible);
  };

  const handleSignOut = () => {
    signOut(setGlobalState, () => navigate('/'));
    setShowModal(true);
  };

  const handleSignClick = () => {
    if (!auth.isLoggedIn) {
      setIsSigningIn(true); // Default to sign in when opening modal
      setShowModal(true);
    } else {
      handleSignOut();
    }
  };

  const switchModal = () => setIsSigningIn(!isSigningIn);

  return (
    <nav className='nav'>
      {showModal && !isPasswordResetRoute && ( // Only show modal if not on password reset route
        isSigningIn ? (
          <SignInModal onClose={() => setShowModal(false)} switchToSignUp={switchModal} />
        ) : (
          <SignUpModal onClose={() => setShowModal(false)} switchToSignIn={switchModal} />
        )
      )}
      <Link to='/'>
        <div className='nav-logo-brand'>
          <img src='/images/bank-logo.png' alt='Bank Logo' className='nav-logo' />
          <div className='brand-name'>
            <span>SecureSentinel</span>
            <span>Bank</span>
          </div>
        </div>
      </Link>
      <div className={`nav-items ${isMenuVisible ? 'visible' : ''}`}>
        <Link to='about'>
          <button>About</button>
        </Link>
        <Link to='careers'>
          <button>Careers</button>
        </Link>
        <Link to='help'>
          <button>Help</button>
        </Link>
      </div>
      <button className='golden-btn' id='login_btn' onClick={handleSignClick}>
        {auth.isLoggedIn ? 'Sign Out' : 'Sign In'}
      </button>
      <div className='hamburger-menu' onClick={toggleMenu}>
        <FontAwesomeIcon icon={faBars} />
      </div>
    </nav>
  );
};
export default Navigation;
