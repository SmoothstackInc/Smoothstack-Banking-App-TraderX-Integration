export function processTransactionData(transactions) {
  return transactions.map((transaction) => ({
    date: formatDate(transaction.dateTime),
    balance: transaction.closingBalance,
  }));
}

export function formatDate(dateString) {
  const date = new Date(dateString);
  const formatter = new Intl.DateTimeFormat('en', { year: '2-digit', month: 'short', day: 'numeric' });
  return formatter.format(date);
}
export function formatDateTransactions(dateString) {
  const date = new Date(dateString);
  const formatter = new Intl.DateTimeFormat('en', { year: 'numeric', month: 'long', day: 'numeric' });
  return formatter.format(date);
}
export function currencyFormatter(value) {
  return `$${Number(value).toLocaleString('en-US', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  })}`;
}
