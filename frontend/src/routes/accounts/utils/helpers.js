import * as yup from 'yup';

export const fetchCountries = async () => {
  const response = await fetch('https://restcountries.com/v3/all');
  if (!response.ok) {
    throw new Error('Failed to fetch countries');
  }
  const data = await response.json();
  return data.map((country) => country.name.official);
};

export const validationSchema = yup.object({
  firstName: yup
    .string()
    .required('First name is required')
    .matches(/^[a-zA-Z\s-]+$/, 'Invalid first name'),
  lastName: yup
    .string()
    .required('Last name is required')
    .matches(/^[a-zA-Z\s-]+$/, 'Invalid last name'),
  address1: yup.string().required('Address line 1 is required'),
  city: yup.string().required('City is required'),
  state: yup.string().required('State is required'),
  zip: yup
    .string()
    .required('Zip code is required')
    .matches(/^\d{5}(-\d{4})?$/, 'Invalid zip code'),
  country: yup.string().required('Country is required'),
  employment: yup.string().required('Employment status is required'),
  income: yup.string().required('Source of income is required'),
  ssn: yup
    .string()
    .required('Social Security Number is required')
    .matches(/^\d{3}-\d{2}-\d{4}$/, 'Invalid SSN'),
});

export const formatSSN = (value) => {
  // Remove non-digit characters
  const numericValue = value.replace(/\D/g, '');
  // Add dashes after the third and fifth digits
  if (numericValue.length > 3 && numericValue.length <= 5) {
    return `${numericValue.slice(0, 3)}-${numericValue.slice(3)}`;
  } else if (numericValue.length > 5) {
    return `${numericValue.slice(0, 3)}-${numericValue.slice(3, 5)}-${numericValue.slice(5, 9)}`;
  }
  return numericValue;
};
