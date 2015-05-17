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
		Motor.A.setSpeed(400);
		Motor.C.setSpeed(400);
		Behavior b1 = new DriveForward();
		Behavior b2 = new DetectWhite();
		Behavior b3 = new Exit();
		Behavior[] behaviorList = { b1, b2, b3 };
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
	private LightSensor light;
//	private UltrasonicSensor sonar;
	private boolean _suppressed = false;
	private boolean active = false;
	private int distance = 255;

	public DetectWhite() {
		light = new LightSensor(SensorPort.S3);
//		sonar = new UltrasonicSensor(SensorPort.S3);
		this.setDaemon(true);
		this.start();
	}

	public void run() {
//		while (true)
//			distance = sonar.getDistance();
	}

	public int takeControl() {
		return Math.max(0, light.readValue() - 40);
	}
	
	public void suppress() {
		_suppressed = true;// standard practice for suppress methods
	}

	public void action() {
		_suppressed = false;
		active = true;
		Sound.beepSequenceUp();

		// Backward for 1000 msec
		LCD.drawString("Drive backward", 0, 3);
		Motor.A.backward();
		Motor.C.backward();
		long now = System.currentTimeMillis();
		while (!_suppressed && (System.currentTimeMillis() < now + 1000)) {
			Thread.yield(); // don't exit till suppressed
		}		

		// Turn
		LCD.drawString("Turn          ", 0, 3);		
		Motor.A.rotate(-180, true);// start Motor.A rotating backward
		Motor.C.rotate(-800, true); // rotate C farther to make the turn
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
