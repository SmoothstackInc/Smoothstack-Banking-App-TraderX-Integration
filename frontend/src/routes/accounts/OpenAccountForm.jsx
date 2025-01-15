import {
  Button,
  Container,
  FormControl,
  Grid,
  InputLabel,
  MenuItem,
  Paper,
  Select,
  TextField,
  Typography,
  Box,
  FormHelperText,
} from '@mui/material';
import { useEffect, useState, useMemo } from 'react';
import { fetchCountries, validationSchema, formatSSN } from './utils/helpers';

const UserInfoForm = ({ onSubmit, appInfo, userData }) => {
  const [countries, setCountries] = useState([]);
  const [userInfo, setUserInfo] = useState({
    ...appInfo,
  });

  const [errors, setErrors] = useState({});

  const handleChange = (event) => {
    const { name, value } = event.target;
    let formattedValue = value;

    if (name === 'ssn') {
      formattedValue = formatSSN(value);
    }

    setUserInfo((prevState) => ({
      ...prevState,
      [name]: formattedValue,
    }));

    if (errors[name]) {
      setErrors((prevState) => ({ ...prevState, [name]: '' }));
    }
  };
  useEffect(() => {
    const fetchCountryData = async () => {
      try {
        const fetchedCountries = await fetchCountries();
        setCountries(fetchedCountries);
      } catch (error) {
        console.error('Error fetching countries:', error);
      }
    };

    fetchCountryData();
  }, []);
  const sortedCountries = useMemo(() => countries.sort(), [countries]);

  const handleSubmit = async () => {
    try {
      await validationSchema.validate(userInfo, { abortEarly: false });
      onSubmit(userInfo);
      setErrors({});
    } catch (yupErrors) {
      const newErrors = yupErrors.inner.reduce((acc, curr) => ({ ...acc, [curr.path]: curr.message }), {});
      setErrors(newErrors);
    }
  };

  useEffect(() => {
    if (userData) {
      setUserInfo((prevState) => ({
        ...prevState,
        firstName: userData.firstName || appInfo.firstName,
        lastName: userData.lastName || appInfo.lastName,
        address1: userData.address || appInfo.address1,
      }));
    }
  }, [userData]);

  return (
    <>
      <Container maxWidth='md' sx={{ height: '100%', mb: 6 }}>
        <Paper
          variant='outlined'
          sx={{
            display: 'flex',
            flexDirection: 'column',
            p: { xs: 2, md: 3 },
            height: { xs: 'auto' },
            background: '#262626',
            filter: 'drop-shadow(0px 5px 10px rgba(226, 236, 249, 0.20))',
          }}
        >
          <Typography variant='h4' sx={{ textAlign: 'center', mb: 4 }} gutterBottom>
            Enter Your Information
          </Typography>
          <Grid container spacing={3} sx={{ mb: 2 }}>
            <Grid item xs={12} sm={6}>
              <TextField
                required
                disabled={!!userData.firstName}
                error={!!errors.firstName}
                helperText={errors.firstName}
                id='firstName'
                name='firstName'
                label='First name'
                fullWidth
                variant='filled'
                value={userInfo.firstName}
                onChange={handleChange}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                disabled={!!userData.lastName}
                error={!!errors.lastName}
                helperText={errors.lastName}
                required
                id='lastName'
                name='lastName'
                label='Last name'
                fullWidth
                variant='filled'
                value={userInfo.lastName}
                onChange={handleChange}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                disabled={!!userData.address}
                error={!!errors.address1}
                helperText={errors.address1}
                required
                id='address1'
                name='address1'
                label='Address line 1'
                fullWidth
                variant='filled'
                value={userInfo.address1}
                onChange={handleChange}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                id='address2'
                name='address2'
                label='Address line 2'
                fullWidth
                variant='filled'
                value={userInfo.address2}
                onChange={handleChange}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                error={!!errors.city}
                helperText={errors.city}
                required
                id='city'
                name='city'
                label='City'
                fullWidth
                variant='filled'
                value={userInfo.city}
                onChange={handleChange}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                error={!!errors.state}
                helperText={errors.state}
                required
                id='state'
                name='state'
                label='State/Province/Region'
                fullWidth
                variant='filled'
                value={userInfo.state}
                onChange={handleChange}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                error={!!errors.zip}
                helperText={errors.zip}
                required
                id='zip'
                name='zip'
                label='Zip / Postal code'
                fullWidth
                placeholder='##### or #####-####'
                variant='filled'
                value={userInfo.zip}
                onChange={handleChange}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControl variant='filled' error={!!errors.country} fullWidth>
                <InputLabel id='country-label'>Country</InputLabel>
                <Select
                  required
                  name='country'
                  value={userInfo.country}
                  label='Country'
                  MenuProps={{
                    PaperProps: {
                      style: {
                        maxHeight: 200,
                        maxWidth: 300,
                      },
                    },
                  }}
                  onChange={handleChange}
                >
                  {sortedCountries.map((country, index) => (
                    <MenuItem key={index} value={country}>
                      {country}
                    </MenuItem>
                  ))}
                </Select>
                {errors.country && <FormHelperText>{errors.country}</FormHelperText>}
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControl variant='filled' error={!!errors.employment} fullWidth required>
                <InputLabel id='employment-label'>Employment Status</InputLabel>
                <Select
                  labelId='employment-label'
                  id='employment'
                  name='employment'
                  label='Employment Status'
                  required
                  fullWidth
                  value={userInfo.employment}
                  onChange={handleChange}
                >
                  <MenuItem value='Employed'>Employed</MenuItem>
                  <MenuItem value='Self-Employed'>Self-Employed</MenuItem>
                  <MenuItem value='Not Employed'>Not Employed</MenuItem>
                </Select>
                {errors.employment && <FormHelperText>{errors.employment}</FormHelperText>}
              </FormControl>
            </Grid>

            <Grid item xs={12} sm={6}>
              <FormControl variant='filled' error={!!errors.income} fullWidth required>
                <InputLabel id='income-label'>Source of Income</InputLabel>
                <Select
                  labelId='income-label'
                  id='income'
                  name='income'
                  label='Source of Income'
                  required
                  fullWidth
                  value={userInfo.income}
                  onChange={handleChange}
                >
                  <MenuItem value='Employment'>Employment</MenuItem>
                  <MenuItem value='Unemployment'>Unemployment</MenuItem>
                  <MenuItem value='Social Security'>Social Security</MenuItem>
                  <MenuItem value='Other'>Other</MenuItem>
                </Select>
                {errors.income && <FormHelperText>{errors.income}</FormHelperText>}
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                error={!!errors.ssn}
                helperText={errors.ssn}
                required
                id='ssn'
                name='ssn'
                label='Social Security Number'
                placeholder='XXX-XX-XXXX'
                fullWidth
                variant='filled'
                value={userInfo.ssn}
                onChange={handleChange}
              />
            </Grid>
          </Grid>
          <Box sx={{ mb: 4, mt: 4, display: 'flex', justifyContent: 'flex-end' }}>
            <Button
              variant='contained'
              color='primary'
              sx={{
                width: { sm: '100px', md: '100px' },
                height: '40px',
                fontWeight: 'bold',
                textTransform: 'none',
              }}
              onClick={handleSubmit}
            >
              Next
            </Button>
          </Box>
        </Paper>
      </Container>
    </>
  );
};

export default UserInfoForm;
