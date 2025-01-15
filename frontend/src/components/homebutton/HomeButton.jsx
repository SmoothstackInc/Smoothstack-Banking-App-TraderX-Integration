import { Link } from 'react-router-dom';
import Button from 'react-bootstrap/Button';
import './HomeButton.css';

function HomeButton() {
  return (
    <div className='home-button-div'>
      <Link to='/'>
        <Button className='home-button' variant='danger'>
          Back To Home
        </Button>
      </Link>{' '}
    </div>
  );
}

export default HomeButton;
