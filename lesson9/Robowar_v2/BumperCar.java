//import robowar.Exit;
import java.util.ArrayList;

import lejos.nxt.*;
import lejos.robotics.RegulatedMotor;

/**
 * Demonstration of the Behavior subsumption classes.
 * 
 * Requires a wheeled vehicle with two independently controlled motors connected
 * to motor ports A and C, and a touch sensor connected to sensor port 1 and an
 * ultrasonic sensor connected to port 3;
 * 
 * @author Brian Bagnall and Lawrie Griffiths, modified by Roger Glassey
 *
 *         Uses a new version of the Behavior interface and Arbitrator with
 *         integer priorities returned by takeCaontrol instead of booleans.
 * 
 *         Exit behavior inserted, local distance sampling thread and backward
 *         drive added in DetectWall by Ole Caprani, 23-4-2012
 */
public class BumperCar {

	public static void main(String[] args) {
		Motor.B.setSpeed(2000);	
		Behavior b1 = new DriveForward();
		Behavior b2 = new DetectWhite();
		Behavior b3 = new DetectRobot();
		Behavior b4 = new Exit();
		Behavior[] behaviorList = { b1, b3, b2, b4 };
		Arbitrator arbitrator = new Arbitrator(behaviorList);
		LCD.drawString("Bumper Car", 0, 1);
		Button.waitForAnyPress();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Motor.B.backward();
		arbitrator.start();

		LCD.drawString("Left: ", 0, 0);
		LCD.drawString("Right: ", 0, 1);
	}
}

class DriveForward implements Behavior {

	private boolean _suppressed = false;

	public int takeControl() {
		return 10; // this behavior always wants control.
	}

	public void suppress() {
		_suppressed = true;// standard practice for suppress methods
	}

	public void action() {
		_suppressed = false;
		Motor.A.setSpeed(500);
		Motor.C.setSpeed(500);		
		Motor.A.forward();
		Motor.C.forward();
		LCD.drawString("Drive forward", 0, 2);
		while (!_suppressed) {
			Thread.yield(); // don't exit till suppressed
		}
		Motor.A.stop(); // not strictly necessary, but good programming practice
		Motor.C.stop();
		LCD.drawString("Drive stopped", 0, 2);
	}
}

class DetectWhite extends Thread implements Behavior {
	private LightSensor rightLight;
	private LightSensor leftLight;
	// private UltrasonicSensor sonar;
	private boolean _suppressed = false;
	private boolean active = false;
	private int distance = 255;
	private boolean detectedLeft = false;
	private boolean detectedRight = false;

	public DetectWhite() {
		rightLight = new LightSensor(SensorPort.S3);
		leftLight = new LightSensor(SensorPort.S2);
		// sonar = new UltrasonicSensor(SensorPort.S3);
		this.setDaemon(true);
		this.start();
	}

	public void run() {
		// while (true)
		// distance = sonar.getDistance();
	}

	public int takeControl() {
		detectedLeft = leftLight.readValue() > 50;
		detectedRight = rightLight.readValue() > 50;
		if (detectedLeft || detectedRight)
			return 100;
		return 0;
	}

	public void suppress() {
		_suppressed = true;// standard practice for suppress methods
	}

	public void action() {
		_suppressed = false;
		active = true;
		Sound.beepSequenceUp();
		
		Motor.A.setSpeed(2000);
		Motor.C.setSpeed(2000);

		// Backward for 200 msec
		LCD.drawString("Drive backward", 0, 3);
		Motor.A.backward();
		Motor.C.backward();
		long now = System.currentTimeMillis();
		while (!_suppressed && (System.currentTimeMillis() < now + 200)) {
			Thread.yield(); // don't exit till suppressed
		}

		if (detectedLeft && !detectedRight) {
			// Turn right.
			Motor.A.rotate(500, true);// start Motor.A rotating backward
			Motor.C.rotate(- 500, true);
		} else if (!detectedLeft && detectedRight) {
			// Turn left.
			Motor.A.rotate(- 500, true);// start Motor.A rotating backward
			Motor.C.rotate(500, true);
		} else {
			// Turn 180 degrees.
			Motor.A.rotate(- 750, true);// start Motor.A rotating backward
			Motor.C.rotate(750, true);
		}
		
		while (!_suppressed && Motor.C.isMoving()) {
			Thread.yield(); // don't exit till suppressed
		}
		Motor.A.stop();
		Motor.C.stop();
		LCD.drawString("Stopped       ", 0, 3);
		Sound.beepSequence();
		active = false;
	}
}

class DetectRobot extends Thread implements Behavior {
	private UltrasonicSensor sonarLeft;
	private UltrasonicSensor sonarRight;
	private boolean _suppressed = false;
	private boolean active = false;
	private int distanceLeft = 255;
	private int distanceRight = 255;
	private ArrayList<Integer> leftDistArray = new ArrayList<Integer>();
	private ArrayList<Integer> rightDistArray = new ArrayList<Integer>();
	private boolean detectedLeft = false;
	private boolean detectedRight = false;
	private long cooldownMs = 2000;
	private long detectTime = Long.MAX_VALUE;

	public DetectRobot() {
		sonarLeft = new UltrasonicSensor(SensorPort.S1);
		sonarRight = new UltrasonicSensor(SensorPort.S4);
		this.setDaemon(true);
		this.start();
	}

	public void run() {
		while (true) {
//			LCD.drawInt(distanceLeft, 4, 10, 0);
//			LCD.drawInt(distanceRight, 4, 10, 1);
			leftDistArray.add(sonarLeft.getDistance());
			rightDistArray.add(sonarRight.getDistance());
			if (leftDistArray.size() > 3) leftDistArray.remove(0);
			if (rightDistArray.size() > 3) rightDistArray.remove(0);
			int sum = 0;
			for (Integer i : leftDistArray) {
				sum += i;
			}
			distanceLeft = sum / leftDistArray.size();
			sum = 0;
			for (Integer j : rightDistArray) {
				sum += j;
			}
			distanceRight = sum / rightDistArray.size();
		}
	}

	public int takeControl() {
		if (System.currentTimeMillis() < detectTime + cooldownMs)
			return 0;
		detectedLeft = distanceLeft < 30;
		detectedRight = distanceRight < 30;
		if (detectedLeft || detectedRight)
			return 50;
		return 0;
	}

	public void suppress() {
		_suppressed = true;// standard practice for suppress methods
	}

	public void action() {
		_suppressed = false;
		active = true;
		Motor.A.setSpeed(2000);
		Motor.C.setSpeed(2000);
		if (detectedLeft) {
			Sound.beepSequenceUp();
			Motor.A.rotate(-100, true);
			Motor.C.rotate(100, true);

		} else if (detectedRight) {
			Sound.beepSequence();
			Motor.A.rotate(100, true);
			Motor.C.rotate(-100, true);
		}

		detectTime = System.currentTimeMillis();

		while (!_suppressed && Motor.C.isMoving()) {
			Thread.yield(); // don't exit till suppressed
		}

		Motor.A.stop();
		Motor.C.stop();
		LCD.drawString("Stopped       ", 0, 3);
		// Sound.beepSequence();
		active = false;
	}
}

class Exit implements Behavior {
	private boolean _suppressed = false;

	public int takeControl() {
		if (Button.ESCAPE.isDown())
			return 200;
		return 0;
	}

	public void suppress() {
		_suppressed = true;// standard practice for suppress methods
	}

	public void action() {
		System.exit(0);
	}
}
