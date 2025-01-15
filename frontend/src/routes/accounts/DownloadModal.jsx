import React, { useState, useEffect } from 'react';
import {
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Button,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  Stack,
} from '@mui/material';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import dayjs from 'dayjs';

const DownloadModal = ({ open, onClose, onDownload }) => {
  const [downloadStartDate, setDownloadStartDate] = useState(null);
  const [downloadEndDate, setDownloadEndDate] = useState(null);
  const [fileType, setFileType] = useState('');

  const handleDownload = () => {
    if (downloadStartDate && downloadEndDate && fileType) {
      onDownload({ downloadStartDate, downloadEndDate, fileType });
      onClose();
    }
  };

  const isDownloadDisabled = !downloadStartDate || !downloadEndDate || !fileType;

  useEffect(() => {
    if (!open) {
      setDownloadStartDate(null);
      setDownloadEndDate(null);
      setFileType('');
    }
  }, [open]);
  return (
    <Dialog open={open} onClose={onClose} sx={{ height: '100%' }}>
      <DialogTitle>Select Download Options</DialogTitle>
      <DialogContent>
        <LocalizationProvider dateAdapter={AdapterDayjs}>
          <Stack spacing={2} sx={{ mt: 2 }}>
            <DatePicker
              label='Start Date'
              value={downloadStartDate}
              onChange={(newValue) =>
                setDownloadStartDate(newValue ? dayjs(newValue).format('YYYY-MM-DDTHH:mm:ss') : null)
              }
              shouldDisableDate={(date) => date.isAfter(dayjs())}
              sx={{ width: '100%' }}
            />
            <DatePicker
              label='End Date'
              value={downloadEndDate}
              onChange={(newValue) =>
                setDownloadEndDate(newValue ? dayjs(newValue).format('YYYY-MM-DDTHH:mm:ss') : null)
              }
              shouldDisableDate={(date) =>
                !downloadStartDate || date.isBefore(downloadStartDate) || date.isAfter(dayjs())
              }
              sx={{ width: '100%' }}
            />
            <FormControl fullWidth>
              <InputLabel>File Type</InputLabel>
              <Select value={fileType} label='File Type' onChange={(e) => setFileType(e.target.value)}>
                <MenuItem value='csv'>CSV</MenuItem>
                <MenuItem value='pdf'>PDF</MenuItem>
                <MenuItem value='xlsx'>XLSX</MenuItem>
              </Select>
            </FormControl>
          </Stack>
        </LocalizationProvider>
      </DialogContent>
      <DialogActions>
        <Button variant='outlined' sx={{ color: 'white', textTransform: 'none' }} onClick={onClose}>
          Cancel
        </Button>
        <Button
          variant='contained'
          sx={{ textTransform: 'none', fontWeight: 'bold' }}
          onClick={handleDownload}
          disabled={isDownloadDisabled}
        >
          Download
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default DownloadModal;
