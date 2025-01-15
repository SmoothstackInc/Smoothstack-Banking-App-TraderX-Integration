import Cookies from 'js-cookie';

export const signOut = (setGlobalState, navigateCallback) => {
  Cookies.remove('token');

  setGlobalState({
    userId: null,
    role: null,
    username: null,
  });

  navigateCallback('/');
};
