import React, { useState, useEffect } from 'react';
import { Select, MenuItem, TextField, FormControl } from '@mui/material';
import Pagination from '@mui/material/Pagination';
import BranchCard from './BranchCard';
import { fetchBranches } from '../../../services/BranchService';
import './BranchDisplay.css';

const BranchDisplay = () => {
  const [branches, setBranches] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterBy, setFilterBy] = useState('branchName');
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);

  useEffect(() => {
    const fetchBranchData = async () => {
      const { branches, totalPages } = await fetchBranches(page);
      setBranches(branches);
      setTotalPages(totalPages);
    };
    fetchBranchData();
  }, [page]);

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

  const filteredBranches = branches.filter((branch) =>
    branch[filterBy].toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="branch-display">
      <h2 className="branch-title">Secure Sentinel Bank Branches</h2>

      <div className="branch-filter-row">
        <FormControl className="branch-filter-dropdown">
          <Select
            id="branch-filter"
            value={filterBy}
            onChange={handleFilterChange}
            className="branch-filter-toggle"
          >
            <MenuItem value="branchName">Branch Name</MenuItem>
            <MenuItem value="address1">Address</MenuItem>
          </Select>
        </FormControl>
        <div className="branch-search-container">
          <TextField
            className="branch-filter-text-area"
            placeholder={`Search by ${filterBy}`}
            value={searchTerm}
            onChange={handleSearchChange}
          />
        </div>
      </div>

      <div className="branch-list">
        {branches.length > 0 && (
          <div>
            {filteredBranches.map((branch) => (
              <BranchCard key={branch.branchId} branch={branch} />
            ))}
          </div>
        )}
      </div>
      
      <div className="pagination-container">
        <Pagination
          count={totalPages}
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

export default BranchDisplay;
