import React, { createContext, useContext, useState } from 'react';

export const GlobalStateProvider = ({ children }) => {
  const [globalState, setGlobalState] = useState({
    userId: null,
    role: null,
    username: null,
  });

  return (
    <GlobalStateContext.Provider value={{ globalState, setGlobalState }}>
      {children}
    </GlobalStateContext.Provider>
  );
};

export const GlobalStateContext = createContext();
export const useGlobalState = () => useContext(GlobalStateContext);
