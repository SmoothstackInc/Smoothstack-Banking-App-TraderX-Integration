import React from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {faGear, faCreditCard, faHandHoldingUsd, faUniversity, faChartLine, faPiggyBank, faUserCircle } from '@fortawesome/free-solid-svg-icons';
import { Link } from 'react-router-dom';
import { getUsername } from '../../utils/TokenUtils';


const Sidebar = () => {
    const username = getUsername();
    return (
        <nav className="sidebar__cont">
            <ul className="sidebar">
                <li className="sidebar__items ">
                    <FontAwesomeIcon icon={faUserCircle} className="sidebar_icon" />
                    <Link to="user-profile">{username}</Link>
                </li>
                <li className="sidebar__items ">
                    <FontAwesomeIcon icon={faChartLine} className="sidebar_icon" />
                    <Link to="dashboard">Dashboard</Link>
                </li>

                <li className="sidebar__items ">
                    <FontAwesomeIcon icon={faPiggyBank} className="sidebar_icon" />
                    <Link to="accounts">Accounts</Link>
                </li>

                <li className="sidebar__items ">
                    <FontAwesomeIcon icon={faCreditCard} className="sidebar_icon" />
                    <Link to="cards">Cards</Link>
                </li>

                <li className="sidebar__items ">
                    <FontAwesomeIcon icon={faHandHoldingUsd} className="sidebar_icon" />
                    <Link to="loans">Loans</Link>
                </li>

                <li className="sidebar__items ">
                    <FontAwesomeIcon icon={faUniversity} className="sidebar_icon" />
                    <Link to="branches">Branches</Link>
                </li>
                <li className="sidebar__items ">
                    <FontAwesomeIcon icon={faGear} className="sidebar_icon" />
                    <Link to="user-settings">Settings</Link>
                </li>
            </ul>
        </nav>
    );
};

export default Sidebar;