package ca.mcgill.ecse211.wallfollowing;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * This class is a controller that uses proportional adjustments
 * to speed. The controller creates sharper turns when the robot
 * is farther away from the band center, and less drastic turns
 * when it is closer to the band center.
 * 
 * @author Bijan Sadeghi
 *
 */
public class PController implements UltrasonicController {

  /* Constants */
  private static final int MOTOR_SPEED = 200;
  private static final int FILTER_OUT = 20;
  
  // Proportionality constant for speed correction
  public static final double PROPCONST = 1.0;
  
  // Upper bound on correction to prevent stalling
  public static final int MAXCORRECTION = 50;

  private final int bandCenter;
  private final int bandWidth;
  private int distance;
  private int filterControl;

  public PController(int bandCenter, int bandwidth) {
    this.bandCenter = bandCenter;
    this.bandWidth = bandwidth;
    this.filterControl = 0;

    WallFollowingLab.leftMotor.setSpeed(MOTOR_SPEED);
    WallFollowingLab.rightMotor.setSpeed(MOTOR_SPEED);
    WallFollowingLab.leftMotor.forward();
    WallFollowingLab.rightMotor.forward();
  }

  @Override
  public void processUSData(int distance) {

    // rudimentary filter - toss out invalid samples corresponding to null
    // signal.
    // (n.b. this was not included in the Bang-bang controller, but easily
    // could have).
    //
    if (distance >= 255 && filterControl < FILTER_OUT) {
      // bad value, do not set the distance var, however do increment the
      // filter value
      filterControl++;
    } else if (distance >= 255) {
      // We have repeated large values, so there must actually be nothing
      // there: leave the distance alone
      this.distance = distance;
    } else {
      // distance went below 255: reset filter and leave
      // distance alone.
      filterControl = 0;
      this.distance = distance;
    }

    // Measure distance to the wall. Because the sensor is placed at 45 degrees,
    // the distance to the wall is the cosine.
    int wallDistance = (int) (this.distance * Math.cos(Math.toRadians(45)));
    System.out.println("Wall distance: " + wallDistance);
    
    // Calculate the distance error
    int distError = bandCenter - wallDistance; 
    
    // Calculate the speed correction based on the distance error
    int speedCorrection = calcSpeedCorrection(distError);
    
    // Variables to be used for the left and right wheel speeds
    int leftSpeed, rightSpeed;
    
    // Case where the robot is too close to the wall
    if (wallDistance < (bandCenter - bandWidth)){
    	// Calculate the left and right speeds by subtracting/adding the calculated
    	// speed correction, which is first multiplied by a constant.
    	// The left speed is increased, while the right speed is decreased.
    	leftSpeed = MOTOR_SPEED + 5 * speedCorrection;
    	rightSpeed = MOTOR_SPEED - 3 * speedCorrection;
    	WallFollowingLab.leftMotor.setSpeed(leftSpeed);
    	WallFollowingLab.rightMotor.setSpeed(rightSpeed);
    }
    
    // Case where the robot is too far from the wall
    else if (wallDistance > (bandCenter + bandWidth)){
    	// Calculate the left and right speeds by subtracting/adding the calculated
    	// speed correction, which is first multiplied by a constant.
    	// The left speed is decreased, while the right speed is increased.
    	leftSpeed = MOTOR_SPEED - speedCorrection;
    	rightSpeed = MOTOR_SPEED + 3 * speedCorrection;
    	WallFollowingLab.leftMotor.setSpeed(leftSpeed);
    	WallFollowingLab.rightMotor.setSpeed(rightSpeed);
    }
    
    // Case where the robot is within acceptable distance from the wall
    else {
    	// Set the left and right speeds to the default motor speed
    	WallFollowingLab.leftMotor.setSpeed(MOTOR_SPEED);
    	WallFollowingLab.rightMotor.setSpeed(MOTOR_SPEED);
    }
  }
  
  
  /**
   * Calculates the correction to speed, which
   * is proportional to the robot's distance error.
   * 
   * @param distError
   */
public int calcSpeedCorrection(int distError) {
	  int netError = Math.abs(distError);
	  int correction = (int) (PROPCONST * (double) netError);
	  if (correction >= 100) {
		  correction = MAXCORRECTION;
	  }
	  
	  return correction;
  }

  @Override
  public int readUSDistance() {
    return this.distance;
  }

}
