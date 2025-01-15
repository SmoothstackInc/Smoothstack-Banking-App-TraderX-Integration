export const getMaxDate = () => {
  const today = new Date();
  today.setFullYear(today.getFullYear() - 13);
  const offset = today.getTimezoneOffset();
  const adjustedDate = new Date(today.getTime() - (offset * 60 * 1000));
  return adjustedDate.toISOString().split("T")[0];
};
