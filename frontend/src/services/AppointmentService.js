import axios from 'axios';
import Cookies from 'js-cookie';
import moment from 'moment';

const axiosInstance = axios.create({
  baseURL: '/api/v1',
  headers: {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${Cookies.get('token')}`,
  },
});

export const fetchBranches = async () => {
  try {
    const response = await axiosInstance.get('/branch');
    return Array.isArray(response.data) ? response.data : [];
  } catch (error) {
    console.error('Error fetching branches:', error);
    return [];
  }
};

export const fetchBankers = async (branchId) => {
  try {
    const response = await axiosInstance.get(`/banker?branchId=${branchId}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching bankers:', error);
    return [];
  }
};

export const fetchServiceTypes = async () => {
  try {
    const response = await axiosInstance.get('/serviceType');
    return Array.isArray(response.data) ? response.data : [];
  } catch (error) {
    console.error('Error fetching service types:', error);
    return [];
  }
};

const fetchAppointmentsByBranchAndTimeslot = async (branchId, timeslot) => {
  try {
    const response = await axiosInstance.get(
      `/appointment/filter-branch-timeslot?branchId=${branchId}&timeslot=${timeslot}`
    );
    return Array.isArray(response.data) ? response.data : [];
  } catch (error) {
    console.error('Error fetching appointments:', error);
    throw error;
  }
};

const fetchAppointmentsByBankerAndTimeslot = async (bankerId, timeslot) => {
  try {
    const response = await axiosInstance.get(
      `/appointment/filter-banker-timeslot?bankerId=${bankerId}&timeslot=${timeslot}`
    );
    return Array.isArray(response.data) ? response.data : [];
  } catch (error) {
    console.error('Error fetching appointments:', error);
    throw error;
  }
};

export const checkAvailability = async (branchId, startTimes, selectedDate) => {
  try {
    const bankersResponse = await fetchBankers(branchId);
    const bankersCount = bankersResponse.length;
    const startTimeAvailability = {};

    for (const startTime of startTimes) {
      const formattedDateTime = moment(selectedDate + ' ' + startTime).format(
        'YYYY-MM-DDTHH:mm:ss'
      );
      const appointmentsResponse = await fetchAppointmentsByBranchAndTimeslot(
        branchId,
        formattedDateTime
      );
      startTimeAvailability[startTime] =
        appointmentsResponse.length < bankersCount;
    }

    return startTimeAvailability;
  } catch (error) {
    console.error('Error checking availability:', error);
    return {};
  }
};

export const fetchAvailableBankers = async (branchId, formattedDateTime) => {
  try {
    const bankersResponse = await fetchBankers(branchId);
    const appointmentsResponse = await fetchAppointmentsByBranchAndTimeslot(
      branchId,
      formattedDateTime
    );

    return bankersResponse.filter(
      (banker) =>
        !appointmentsResponse.some(
          (appointment) => appointment.bankerId === banker.bankerId
        )
    );
  } catch (error) {
    console.error('Error fetching available bankers:', error);
    return [];
  }
};

export const createAppointment = async (appointmentData) => {
  try {
    const response = await axiosInstance.post('/appointment', appointmentData);
    return response.data;
  } catch (error) {
    console.error('Error creating appointment:', error);
    throw error;
  }
};

export const checkBankerAvailability = async (bankerId, timeslot) => {
  try {
    const response = await fetchAppointmentsByBankerAndTimeslot(
      bankerId,
      timeslot
    );
    return response;
  } catch (error) {
    console.error('Error checking banker availability:', error);
    throw error;
  }
};
