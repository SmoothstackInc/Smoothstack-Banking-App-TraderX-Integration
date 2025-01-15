import React, { useState, useEffect } from 'react';
import { GoogleMap, Marker, LoadScript } from '@react-google-maps/api';
import BankMarker from './bank-logo.png';
import './BranchMap.css';

const containerStyle = {
  width: '40vh',
  height: '40vh',
};

const options = {
  disableDefaultUI: true,
  zoomControl: true,
};

const BranchMap = ({ lat, lng }) => {
  const [map, setMap] = useState(null);

  const onMapLoad = (map) => {
    setMap(map);
  };

  useEffect(() => {
    if (map) {
      map.panTo({ lat, lng });
    }
  }, [lat, lng, map]);

  return (
    <>
      <div className="map-card">
        <LoadScript
          googleMapsApiKey={import.meta.env.VITE_GOOGLE_MAPS_API_KEY}
          onUnmount={() => setMap(null)}
        >
          <GoogleMap
            mapContainerStyle={containerStyle}
            center={{ lat, lng }}
            zoom={15}
            onLoad={onMapLoad}
            options={options}
          >
            <Marker
              position={{ lat, lng }}
              // icon={{BankMarker}}
              className="bank-marker"
            />
          </GoogleMap>
        </LoadScript>
      </div>
    </>
  );
};

export default BranchMap;
