import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.util.Delay;


public class WallFollower extends Thread {
	private static final int littleTooClose = 28;
	private static final int tooClose = 25;
	private static final int wayTooClose = 22;
	private static final int littleTooFar = 30;
	private static final int tooFar = 32;
	private static final int wayTooFar = 34;

	private boolean running;
	private int sampleInterval = 30; // ms default value
	private UltrasonicSensor us = new UltrasonicSensor(SensorPort.S1);
	private final int  noObject = 255;
	private int distance, x, desiredDistance = 25, // cm, default value
			power, minPower = 50, maxPower = 100; // default values
	private float diff, Pgain = 2.0f; // default value

	private int motorLeft, motorRight, powerLeft, powerRight;

	public WallFollower() {

	}

	public void go() {
		running = true;
		this.start();
	}

	public void stop() {
		running = false;
	}

	public void run() {
		x = distance = us.getDistance();		
		diff = 0;

		while ( running ) {		   
			distance = us.getDistance();								

			diff = x - distance;
			x = distance;
			motorLeft = Car.forward;
			motorRight = Car.forward;
			powerLeft = powerRight = 75;
			if (x <= littleTooClose) { // Close to the wall
				if (x <= wayTooClose) {
					motorRight = Car.backward;
					powerLeft = 65;
					powerRight = 65;
				} else if (x <= tooClose) {
					powerRight = 55;
				} else if (x <= littleTooClose) {
					powerRight = 65;
				}
			} else { // Far from the wall
				if (x >= wayTooFar) {
					if (diff < 2) {
						powerLeft = 55;
					} else {
						powerLeft = 55;
					}
				} else if (x >= tooFar) {
					powerLeft = 60;
				} else if (x >= littleTooFar) {
					powerLeft = 65;					
				}
			}

			Car.controlLeft(powerLeft, motorLeft);
			Car.controlRight(powerRight, motorRight);				
			
			//Delay.msDelay(sampleInterval);

		}
	}

	public int getDistance() {
		return distance;
	}
	
	public int getLeftPower()
    {
        return powerLeft;
    }
	
	public int getRightPower()
    {
        return powerRight;
    }
}