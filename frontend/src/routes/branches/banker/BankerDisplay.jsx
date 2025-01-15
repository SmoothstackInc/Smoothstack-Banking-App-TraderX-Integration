import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { TextField, MenuItem, Select, FormControl } from '@mui/material';
import Pagination from '@mui/material/Pagination';
import BankerCard from './BankerCard';
import { fetchBankers } from '../../../services/BranchService';
import './BankerDisplay.css';

const BankerDisplay = () => {
  const [bankers, setBankers] = useState([]);
  const { branchId } = useParams();
  const [searchTerm, setSearchTerm] = useState('');
  const [filterBy, setFilterBy] = useState('jobtitle');
  const [page, setPage] = useState(1);
  const itemsPerPage = 10;

  useEffect(() => {
    const fetchBankerData = async () => {
      try {
        const { bankers } = await fetchBankers(branchId);
        setBankers(bankers);
      } catch (error) {
        console.error('Failed to fetch bankers:', error);
        setBankers([]);
      }
    };

    fetchBankerData();
  }, [branchId]);

  const handleFilterChange = (e) => {
    setFilterBy(e.target.value);
    setSearchTerm('');
  };

  const handleSearchChange = (e) => {
    setSearchTerm(e.target.value);
  };

  const handlePageChange = (event, value) => {
    setPage(value);
  };

  const filteredBankers = bankers.filter((banker) => {
    const fullName = `${banker.firstName} ${banker.lastName}`.toLowerCase();

    if (filterBy === 'jobtitle') {
      return banker.jobTitle.toLowerCase().includes(searchTerm.toLowerCase());
    } else if (filterBy === 'name') {
      return fullName.includes(searchTerm.toLowerCase());
    }

    return false;
  });

  const paginatedBankers = bankers.slice((page - 1) * itemsPerPage, page * itemsPerPage);

  return (
    <div className="banker-display">
      <h2 className="banker-title">Our Team</h2>

      <div className="banker-filter-row">
        <FormControl className="banker-filter-dropdown">
          <Select
            id="banker-filter"
            value={filterBy}
            onChange={handleFilterChange}
            className="banker-filter-toggle"
          >
            <MenuItem value="jobtitle">Job Title</MenuItem>
            <MenuItem value="name">Name</MenuItem>
          </Select>
        </FormControl>

        <div className="banker-search-container">
          <TextField
            className="banker-filter-text-area"
            label={`Search by ${filterBy}`}
            value={searchTerm}
            onChange={handleSearchChange}
          />
        </div>
      </div>

      <div className="banker-list">
        {bankers.length > 0 ? (
          <div>
            {paginatedBankers.map((banker) => (
              <BankerCard key={banker.bankerId} banker={banker} />
            ))}
          </div>
        ) : (
          <div>No bankers available</div>
        )}
      </div>
      <div className="pagination-container">
        <Pagination
          className='pagination-element'
          count={Math.ceil(filteredBankers.length / itemsPerPage)}
          page={page}
          onChange={handlePageChange}
          variant="outlined"
          shape="rounded"
          size="large"
        />
      </div>
    </div>
  );
};

export default BankerDisplay;
