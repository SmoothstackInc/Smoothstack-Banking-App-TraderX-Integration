import RobotConfused from './robot-confused.gif'
import './NotFound.css';

function NotFound() {

  return (
    <div id="not-found" className="not-found">
      <img src={RobotConfused} className="robot-confused" alt="Confused Robot" />
      <h3>This is the 404 page.</h3>
    </div>
  );
}

export default NotFound;