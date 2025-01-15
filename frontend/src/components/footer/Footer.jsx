import React from 'react';
import { Link } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { FaMapMarkerAlt, FaEnvelope, FaPhone } from 'react-icons/fa';
import { faFacebookF, faTwitter, faLinkedinIn } from '@fortawesome/free-brands-svg-icons';

function Footer() {
  return (
    <footer className="footer">
      <div className="footer-top">
        <img src='/images/bank-logo.png' alt='Bank Logo' className='footer-logo' />
        <h2 className="footer-title">SecureSentinel Bank</h2>
      </div>
      <div className="footer-center">
        <a href="mailto:" className="footer-contact">
          <FaEnvelope size={25} />
          SecureSentinel Bank
        </a>
        <div className="footer-contact">
          <FaPhone size={25} />
          +91 91813 23 2309
        </div>
        <div className="footer-contact">
          <FaMapMarkerAlt size={25} />
          Hudson Yards NYC
        </div>
      </div>
      <div className="footer-bottom">
        <section className='footer-bottom-icons'>
          <a href="https://facebook.com" className="icon-link">
            <FontAwesomeIcon icon={faFacebookF} />
          </a>
          <a href="https://twitter.com" className="icon-link">
            <FontAwesomeIcon icon={faTwitter} />
          </a>
          <a href="https://linkedin.com" className="icon-link">
            <FontAwesomeIcon icon={faLinkedinIn} />
          </a>
        </section>
        <section>SecureSentinel All Rights Reserved</section>
        <div className="footer-links">
          <Link to="/privacy" className="footer-link">Privacy Policy</Link>
          <Link to="/terms" className="footer-link">Terms of Service</Link>
        </div>
      </div>
    </footer>
  );
}

export default Footer;
