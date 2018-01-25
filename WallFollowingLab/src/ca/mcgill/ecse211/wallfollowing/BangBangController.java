package ca.mcgill.ecse211.wallfollowing;

import java.util.concurrent.TimeUnit;

import lejos.hardware.motor.*;

/**
 * This class is a controller that uses the "Bang Bang" 
 * approach for wall following. Within each case (too close to wall,
 * within acceptable distance from band center, and too far),
 * the left and right motor speeds are set to strict values.
 * 
 * @author Swaroop Satyanarayan
 *
 */
public class BangBangController implements UltrasonicController {

  private final int bandCenter;
  private final int bandwidth;
  private final int motorLow;
  private final int motorHigh;
  private int distance;

public BangBangController(int bandCenter, int bandwidth, int motorLow, int motorHigh) {
    // Default Constructor
    this.bandCenter = bandCenter;
    this.bandwidth = bandwidth;
    this.motorLow = motorLow;
    this.motorHigh = motorHigh;
    WallFollowingLab.leftMotor.setSpeed(motorHigh); // Start robot moving forward
    WallFollowingLab.rightMotor.setSpeed(motorHigh);
    WallFollowingLab.leftMotor.forward();
    WallFollowingLab.rightMotor.forward();
  }

  @Override
  public void processUSData(int distance) {
    this.distance = distance;
    
    // Measure distance to the wall. Because the sensor is placed at 45 degrees,
    // the distance to the wall is the cosine.
    int wallDistance = (int) (this.distance * Math.cos(Math.toRadians(45)));
    
    // Case where the robot is too close to the wall
    if (wallDistance < (bandCenter - bandwidth)) {
    	WallFollowingLab.leftMotor.setSpeed(motorHigh + 50);  // 350
    	WallFollowingLab.rightMotor.setSpeed(motorLow - 150); // 50
    }
    
    // Case where the robot is too far from the wall
    else if (wallDistance > (bandCenter + bandwidth)) {
    	
    	// Apply 50 ms delay before turning towards the wall (turning left). Helps in ignoring gaps.
    	try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	WallFollowingLab.leftMotor.setSpeed(motorLow - 50);  // 150
    	WallFollowingLab.rightMotor.setSpeed(motorHigh + 75);// 375
    }
    
    // Case where the robot is within acceptable distance from the wall
    else {
    	WallFollowingLab.leftMotor.setSpeed(motorLow);
    	WallFollowingLab.rightMotor.setSpeed(motorLow);
    }
    
  }

  @Override
  public int readUSDistance() {
    return this.distance;
  }
}
