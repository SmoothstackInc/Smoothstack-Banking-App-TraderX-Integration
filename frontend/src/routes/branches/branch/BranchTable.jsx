import React, { useState, useEffect, useMemo } from 'react';

import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TablePagination,
  TextField,
  CircularProgress,
} from '@mui/material';

import BranchCard from './BranchCard';
import { fetchBranches } from '../../../services/BranchService';

import './BranchTable.css';

const formatAddress = (branch) => {
  const { address1, address2, city, state } = branch;
  return `${address1}, ${address2 ? address2 + ', ' : ''}${city}, ${state}`;
};

// Haversine formula
const getDistance = (lat1, lon1, lat2, lon2) => {
  const toRadians = (degrees) => (degrees * Math.PI) / 180;

  const R = 6371;
  const dLat = toRadians(lat2 - lat1);
  const dLon = toRadians(lon2 - lon1);
  const a =
    Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos(toRadians(lat1)) *
      Math.cos(toRadians(lat2)) *
      Math.sin(dLon / 2) *
      Math.sin(dLon / 2);
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

  return R * c;
};

const calculateDistances = (branches, userLocation) => {
  if (!userLocation) return branches;

  return branches.map((branch) => {
    if (branch.lat && branch.lng) {
      const distance = getDistance(
        userLocation.lat,
        userLocation.lng,
        branch.lat,
        branch.lng
      );
      console.log(
        `Calculated distance for branch ${branch.branchName}: ${distance}`
      );
      return { ...branch, distance };
    } else {
      console.log(`Missing lat/lng for branch ${branch.branchName}`);
      return { ...branch, distance: null };
    }
  });
};

const BranchTable = () => {
  const [branches, setBranches] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [totalPages, setTotalPages] = useState(1);
  const [selectedBranch, setSelectedBranch] = useState(null);
  const [userLocation, setUserLocation] = useState(null);
  const [loading, setLoading] = useState(true);
  const [userHasSelected, setUserHasSelected] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    const handleSuccess = (position) => {
      setUserLocation({
        lat: position.coords.latitude,
        lng: position.coords.longitude,
      });
    };

    const handleError = () => {
      setLoading(false);
    };

    navigator.geolocation.getCurrentPosition(handleSuccess, handleError);

    const timeoutId = setTimeout(() => {
      setLoading(false);
    }, 10000);

    return () => clearTimeout(timeoutId);
  }, []);

  useEffect(() => {
    const fetchBranchesData = async () => {
      try {
        const response = await fetchBranches(currentPage);
        setBranches(response.branches);
        setTotalPages(response.totalPages);
      } catch (error) {
        console.error('Error fetching branches:', error);
      } finally {
        setLoading(false);
      }
    };
    fetchBranchesData();
  }, [currentPage]);

  const branchesWithDistances = useMemo(() => {
    if (userLocation) {
      return calculateDistances(branches, userLocation).sort(
        (a, b) => (a.distance ?? Infinity) - (b.distance ?? Infinity)
      );
    }
    return branches;
  }, [branches, userLocation]);

  const closestBranch = useMemo(() => {
    if (userLocation && branches.length > 0) {
      return branchesWithDistances[0];
    }
    return null;
  }, [branchesWithDistances, userLocation, branches.length]);

  useEffect(() => {
    if (!userHasSelected) {
      if (closestBranch) {
        setSelectedBranch(closestBranch);
        // reload table w/ branchesWithDistance
      } else if (branches.length > 0) {
        setSelectedBranch(branches[0]);
      }
    }
  }, [closestBranch, branches, userHasSelected]);

  const handlePageChange = (event, newPage) => {
    setCurrentPage(newPage);
  };

  const handleRowsPerPageChange = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setCurrentPage(0);
  };

  const handleBranchClick = (branch) => {
    setSelectedBranch(branch);
    setUserHasSelected(true);
  };

  const handleSearchChange = (e) => {
    setSearchTerm(e.target.value);
  };

  const filteredBranches = branches.filter((branch) => {
    const branchInfo =
      `${branch.branchName} ${branch.address1} ${branch.address2} ${branch.city} ${branch.state}`.toLowerCase();
    return branchInfo.includes(searchTerm.toLowerCase());
  });

  if (loading) {
    return <CircularProgress />;
  }

  return (
    <div className="branch-page">
      <h2 className="branch-title">Secure Sentinel Bank Branches</h2>
      <div className="card-table-row">
        {selectedBranch && <BranchCard branch={selectedBranch} />}
        <TableContainer>
          <Table
            style={{
              width: '85%',
              boxShadow: 'inset 0 0 2px 0 var(--primary-light)',
            }} 
            className="branch-table"
          >
            <TableHead>
              <TableRow>
                <TableCell
                  style={{
                    color: 'var(--title)',
                  }}
                >
                  Branch Name
                </TableCell>
                <TableCell
                  style={{
                    color: 'var(--title)',
                  }}
                  className="branch-table-address"
                >
                  Address
                </TableCell>
                <TableCell
                  style={{
                    color: 'var(--title)',
                  }}
                >
                  Distance (km)
                </TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {filteredBranches.slice(0, rowsPerPage).map((branch) => (
                <TableRow
                  key={branch.branchId}
                  onClick={() => handleBranchClick(branch)}
                >
                  <TableCell className="branch-tablecell">
                    <span
                      style={{
                        cursor: 'pointer',
                        color: 'var(--title)',
                      }}
                    >
                      {branch.branchName}
                    </span>
                  </TableCell>
                  <TableCell
                    style={{
                      color: 'var(--contrast-lighter)',
                    }}
                    className="branch-table-address"
                  >
                    {formatAddress(branch)}
                  </TableCell>
                  <TableCell
                    style={{
                      color: 'var(--contrast-lighter)',
                    }}
                  >
                    {branch.distance !== null && branch.distance !== undefined
                      ? branch.distance.toFixed(2)
                      : 'N/A'}
                    {/* WHEN */}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
          <div className="filter-pagination-row">
            <div className="branch-filter-row">
              <TextField
                className="branch-filter-text-area"
                placeholder="Search"
                value={searchTerm}
                onChange={handleSearchChange}
              />
            </div>
            <TablePagination
              rowsPerPageOptions={[10, 25, 50]}
              component="div"
              count={totalPages * rowsPerPage}
              rowsPerPage={rowsPerPage}
              page={currentPage}
              onPageChange={handlePageChange}
              onRowsPerPageChange={handleRowsPerPageChange}
              style={{
                color: 'var(--contrast-light)',
              }}
            />
          </div>
        </TableContainer>
      </div>
    </div>
  );
};

export default BranchTable;
