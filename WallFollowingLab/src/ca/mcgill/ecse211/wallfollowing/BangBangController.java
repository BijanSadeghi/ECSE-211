package ca.mcgill.ecse211.wallfollowing;

import lejos.hardware.motor.*;

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
    int diagonalDistance = (int) (distance * Math.cos(Math.toRadians(37))); // Measure distance to the wall
    
    // Too close to wall
    if (distance < (bandCenter - bandwidth)){
    	WallFollowingLab.leftMotor.setSpeed(motorHigh + 50); //increase left speed by 50 more for sharper right turn
    	WallFollowingLab.rightMotor.setSpeed(motorLow - 75); // decrease right speed by 25 for sharper right turn
    }
    // Too far from wall
    else if (distance > (bandCenter + bandwidth)){
    	WallFollowingLab.leftMotor.setSpeed(motorLow);
    	WallFollowingLab.rightMotor.setSpeed(motorHigh + 50);
    }
    // Acceptable distance from wall
    else {
    	WallFollowingLab.leftMotor.setSpeed(motorHigh);
    	WallFollowingLab.rightMotor.setSpeed(motorHigh);
    }
    
  }

  @Override
  public int readUSDistance() {
    return this.distance;
  }
}
