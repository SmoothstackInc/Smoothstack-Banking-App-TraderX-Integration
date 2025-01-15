import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useGlobalState } from '../GlobalStateContext';
import { isTokenValid } from '../TokenUtils';
import { signOut } from '../Session'; 

const useTokenValidation = () => {
  const navigate = useNavigate();
  const { setGlobalState } = useGlobalState();

  useEffect(() => {
    const interval = setInterval(() => {
      if (!isTokenValid()) {
        signOut(setGlobalState, navigate);
      }
    }, 600000); // Check every 10 min

    return () => clearInterval(interval);
  }, [setGlobalState, navigate]);
};

export default useTokenValidation;
