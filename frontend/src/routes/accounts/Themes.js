import { createTheme } from '@mui/material/styles';

const theme = createTheme({
  palette: {
    primary: {
      main: '#ecb300',
    },
    secondary: {
      main: '#FFF',
    },
  },
  typography: {
    fontFamily: 'Lexend, sans-serif',
    allVariants: {
      color: '#FFF',
    },
    h1: {
      color: '#ecb300',
    },
    h2: {
      color: '#ecb300',
    },
    h3: {
      color: '#ecb300',
    },
    h4: {
      color: '#ecb300',
    },
    h5: {
      color: '#ecb300',
    },
    h6: {
      color: '#ecb300',
    },
  },
  components: {
    MuiTextField: {
      styleOverrides: {
        root: {
          '& input': {
            color: '#FFF',
            fontFamily: 'Lexend, sans-serif',
          },
          '& label': {
            color: '#FFF',
          },
          '& .MuiOutlinedInput-root': {
            '& fieldset': {
              borderColor: '#FFF',
            },
            '&:hover fieldset': {
              borderColor: '#FFF',
            },
            '&.Mui-focused fieldset': {
              borderColor: '#FFF',
            },
          },
          '& .Mui-disabled': {
            '-webkit-text-fill-color': '#898989 !important',
          },
        },
      },
    },
    MuiPaper: {
      styleOverrides: {
        root: {
          // This will target the dropdown menus
          backgroundColor: '#262626',
        },
      },
    },
    MuiSelect: {
      styleOverrides: {
        select: {
          background: '#242424',
          color: '#FFF',
          '&:focus': {
            background: '#242424', // maintains background color even after selection
          },
        },
        icon: {
          color: '#FFF', // changes the dropdown icon color
        },
      },
    },
    MuiMenuItem: {
      styleOverrides: {
        root: {
          '&.Mui-selected': {
            backgroundColor: '#181818', // background color when item is selected
            color: '#FFF',
            '&:hover': {
              backgroundColor: '#181818', // background color on hover
            },
          },
          '&:hover': {
            backgroundColor: '#181818', // background color on hover
            color: '#FFF',
          },
        },
      },
    },
    MuiInputLabel: {
      styleOverrides: {
        root: {
          color: '#FFF',
          '&.Mui-focused': {
            color: '#FFF',
          },
        },
      },
    },
    MuiListItemText: {
      styleOverrides: {
        primary: {
          color: '#F4D874',
        },
        secondary: {
          color: '#FFF',
        },
      },
    },
    MuiTableCell: {
      styleOverrides: {
        root: {
          color: '#FFF',
        },
      },
    },
    MuiTableHead: {
      styleOverrides: {
        root: {
          '& .MuiTableCell-root': {
            color: '#ecb300',
            fontSize: '24px',
          },
        },
      },
    },
    MuiPickersDay: {
      styleOverrides: {
        root: {
          color: '#FFF', // Change the text color of the days to white
        },
        daySelected: {
          backgroundColor: '#F4D874', // Background color for selected date
        },
        current: {
          color: '#F4D874', // Color for the current day
        },
      },
    },
    MuiStepLabel: {
      styleOverrides: {
        label: {
          '&.Mui-active': {
            color: '#F4D874',
            fontWeight: 'bold',
          },

          '&.Mui-completed': {
            color: 'green',
          },
        },
      },
    },
    MuiStepIcon: {
      styleOverrides: {
        root: {
          color: 'lightgray',
          '&.Mui-active': {
            color: '#F4D874',
          },
          '&.Mui-completed': {
            color: 'green',
          },
        },
      },
    },
  },
});
export default theme;
