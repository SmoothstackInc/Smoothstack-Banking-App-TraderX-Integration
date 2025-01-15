import { useEffect, useState } from 'react';
import { Chart } from "react-google-charts";
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { getUsername } from '../../utils/TokenUtils';
import SecondaryBtnComponent from '../../components/buttons/SecondaryBtnComponent'

export const data = [
    ["Date", "Deposits", "Spending", "Total Balance"],
    ["2024-02-01", 1500, -150, 1350],
    ["2024-02-02", 500, -440, 1410],
    ["2024-02-03", 1340, -290, 2460],
    ["2024-02-04", 1500, -600, 3360],
    ["2024-02-05", 700, -250, 3360],
    ["2024-02-06", 1600, -1500, 3810],
    ["2024-02-07", 800, -50, 3910],
];

export const options = {
    curveType: "function",
    legend: { position: "bottom", textStyle: { color: '#FFFFF0' } },
    colors: ['#d0ec00', '#ec4800', '#eca600',],
    backgroundColor: "#3C3C3C",
    hAxis: {
        title: "Date",
        titleTextStyle: { color: '#FFFFF0' },
        textStyle: { color: '#FFFFF0' }
    },
    vAxis: {
        title: "Amount ($)",
        titleTextStyle: { color: '#FFFFF0' },
        textStyle: { color: '#FFFFF0' }
    },
};

export const data2 = [
    ['Type', 'Amount'],
    ['Outstanding Balance', 2000],
    ['Available', 5000],
];

export const options2 = {
    backgroundColor: "#3C3C3C",
    legend: { position: "bottom", textStyle: { color: '#FFFFF0' } },
    is3D: true,
    colors: ['#fef167', '#eca600'],
    pieSliceTextStyle: {
        color: 'black',
        bold: true,
    },
};

export const data3 = [
    ["State", "Available branches"],
    ["California", 191],
    ["Texas", 148],
    ["New York", 197],
    ["New Jersey", 185],
    ["Pennsylvania", 125],
    ["Utah", 99],
    ["Florida", 160],
    ["Illinois", 35],
    ["Georgia", 110],
    ["Montana", 59],
    ["Alaska", 1],
    ["Maine", 25],
    ["Virginia", 172],
    ["Maryland", 65],
    ["Washington", 41],
    ["North Carolina", 64],
    ["South Carolina", 88],
];

export const options3 = {
    region: "US",
    resolution: "provinces",
    colorAxis: { colors: ["#fef167", "#fe7a00"] },
    backgroundColor: "#3C3C3C",
    datalessRegionColor: "#f7f098",
    defaultColor: "#eca600",
};

export default function Dashboard() {
    const navigate = useNavigate();
    const [isVerified, setIsVerified] = useState(true);
    const [isLoading, setIsLoading] = useState(true);
    const [message, setMessage] = useState('');
    const [isResending, setIsResending] = useState(false);

    const fetchVerificationStatus = async () => {
        const username = getUsername();
        if (!username) {
            setMessage('Username not found.');
            setIsLoading(false);
            return;
        }
        try {
            const response = await axios.get(`/api/v1/auth/verification-status`, { params: { emailOrUsername: username } });
            setIsVerified(response.data);
        } catch (error) {
            console.error('Failed to fetch verification status:', error);
            setMessage('Failed to fetch verification status.');
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        fetchVerificationStatus();
    }, []);

    const handleChartClick = (event) => {
        const chartType = event.currentTarget.getAttribute('data-chart-type');
        console.log("Chart Type Clicked:", chartType);

        switch (chartType) {
            case 'lineChart':
                navigate('/accounts');
                break;
            case 'pieChart':
                navigate('/cards');
                break;
            case 'geoChart':
                navigate('/branches');
                break;
            default:
                break;
        }
    };

    const requestNewLink = async () => {
        const username = getUsername();
        if (!username) {
            setMessage('Username not found.');
            return;
        }

        setIsResending(true);

        try {
            const response = await axios.post('/api/v1/auth/resend-confirmation', null, { params: { emailOrUsername: username } });
            setMessage('A new verification link has been sent to your email.');
        } catch (error) {
            setMessage('Failed to send a new verification link. Please try again later.');
        } finally {
            setIsResending(false);
        }
    };

    if (isLoading) {
        return <div>Loading...</div>;
    }

    return (
        <div className="dashboard">
            {!isVerified && (
                <div className="verification-message">
                    <p>Your account is not verified. Please check your email to verify your account.</p>
                    {message && <p>{message}</p>}
                    <SecondaryBtnComponent
                        buttonText={isResending ? "Loading..." : "Resend Verification Link"}
                        onClick={requestNewLink}
                    />
                </div>
            )}
            <div className="wrapper">
                <div className="single-chart-wrapper" data-chart-type="lineChart" onClick={handleChartClick}>
                    <Chart
                        className="deposits"
                        chartType="LineChart"
                        width="650px"
                        height="400px"
                        data={data}
                        options={options}
                    />
                    <h2>Financial Activity Overview</h2>
                </div>
                <hr />
                <div className="single-chart-wrapper" data-chart-type="pieChart" onClick={handleChartClick}>
                    <Chart
                        className="pie"
                        chartType="PieChart"
                        data={data2}
                        options={options2}
                        width="30%"
                        height="400px"
                    />
                    <h2 className="credit-card-title"> Credit Card Utilization</h2>
                </div>
            </div>
            <hr />
            <div className="single-chart-wrapper" data-chart-type="geoChart" onClick={handleChartClick}>
                <Chart
                    className="geo-chart"
                    chartType="GeoChart"
                    width="auto"
                    height="400px"
                    data={data3}
                    options={options3}
                />
                <h2 className="geo-chart-title">Available branches</h2>
            </div>
        </div>
    );
}
