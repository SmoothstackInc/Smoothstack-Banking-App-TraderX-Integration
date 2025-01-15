const selectedStyles = {
  color: '#FFF',
  '& .MuiSelect-select': {
    borderRadius: '20px',
    bgcolor: '#181818',
    '&:focus': {
      background: '#181818',
    },
  },
  '& .MuiOutlinedInput-notchedOutline': {
    borderColor: '#F4D874',
  },
  '&:hover': {
    bgcolor: '#F4D874',
    '& .MuiOutlinedInput-notchedOutline': {
      borderColor: '#F4D874',
    },
  },
  '&.Mui-focused .MuiOutlinedInput-notchedOutline': {
    borderColor: '#F4D874',
  },
  alignSelf: 'flex-end',
  borderRadius: '20px',
  textAlign: 'center',
};

export default selectedStyles;
