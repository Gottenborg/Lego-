import lejos.nxt.*;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Move;

public class PilotAvoid {
	private SlaveIOStreams PC;

	double mult = 1.007;
	double leftWheelDiameter = 55 * mult, rightWheelDiameter = 54.84 * mult,
			trackWidth = 165;
	double travelSpeed = 50, rotateSpeed = 45;
	NXTRegulatedMotor left = Motor.A;
	NXTRegulatedMotor right = Motor.C;

	DifferentialPilot pilot = new DifferentialPilot(leftWheelDiameter,
			rightWheelDiameter, trackWidth, left, right, false);

	public PilotAvoid(boolean usb) {
		PC = new SlaveIOStreams(usb);
		PC.open();

		pilot.setTravelSpeed(travelSpeed);
		pilot.setRotateSpeed(rotateSpeed);
	}

	public void sendMove(Move move) {
		PC.output((move.getMoveType() == Move.MoveType.TRAVEL ? 0 : 1));
		PC.output(move.getDistanceTraveled());
		PC.output(move.getAngleTurned());
	}

	public void travel(double distance) {
		pilot.travel(distance, true);
	}

	public void rotate(double angle) {
		pilot.rotate(angle, true);
	}

	public void go() {
		Sound.beep();
		while (!Button.ENTER.isDown())
			Thread.yield();
		Sound.twoBeeps();

		travel(500);
		rotate(90);
		travel(200);
		rotate(-90);
		travel(500);

		/*
		 * travel(500); rotate(90); travel(200); rotate(-90); travel(500);
		 * rotate(90); travel(200); rotate(90); travel(700); rotate(90);
		 * travel(200);
		 */

		while (!Button.ENTER.isDown())
			Thread.yield();

		LCD.clear();
		LCD.drawString("Closing", 0, 0);
		if (PC.close())
			LCD.drawString("Closed", 0, 0);
		try {
			Thread.sleep(2000);
		} catch (Exception e) {
		}
	}

	public static void main(String[] args) {
		PilotAvoid pilot = new PilotAvoid(false);

		LCD.clear();
		LCD.drawString("PilotRouteMM", 0, 0);
		// route.go();
		
		Sound.beep();
		while (!Button.ENTER.isDown())
			Thread.yield();
		Sound.twoBeeps();
		
		Behavior b1 = new FollowRoute(pilot);
		Behavior b2 = new AvoidObstacle(pilot);		
		Behavior[] behaviorList = { b1, b2};
		Arbitrator arbitrator = new Arbitrator(behaviorList);
		arbitrator.start();
	}	
}

class FollowRoute implements Behavior {
	private static final int TRAVEL = 0;
	private static final int ROTATE = 1;	
	
	private boolean _suppressed = false;
	private PilotAvoid pilot;
	private int stepsDone = 0;

	public FollowRoute(PilotAvoid pilot) {
		this.pilot = pilot;
	}

	public int takeControl() {
		return 10; // this behavior always wants control.
	}

	public void suppress() {
		_suppressed = true;// standard practice for suppress methods
	}

	public void action() {
		_suppressed = false;
		
		if (stepsDone < 1 && !_suppressed) {
			doStep(TRAVEL, 500);			
		}
		if (stepsDone < 2 && !_suppressed) {
			doStep(ROTATE, 90);			
		}
		if (stepsDone < 3 && !_suppressed) {
			doStep(TRAVEL, 200);			
		}
		if (stepsDone < 4 && !_suppressed) {
			doStep(ROTATE, -90);			
		}
		if (stepsDone < 5 && !_suppressed) {
			doStep(TRAVEL, 500);			
		}
		
		Motor.A.stop(true); // not strictly necessary, but good programming practice
		Motor.C.stop(true);		
	}
	
	public void doStep(int moveType, double value) {
		switch (moveType) {
		case TRAVEL : pilot.travel(value); break;
		case ROTATE : pilot.rotate(value); break;
		default : System.err.println("Invalid moveType in doStep(): " + moveType); return;
		}		
		stepsDone++;
		while (!_suppressed && Motor.A.isMoving()) {			
			Thread.yield(); // don't exit till suppressed
		}
		pilot.sendMove(pilot.pilot.getMovement());
	}
}

class AvoidObstacle extends Thread implements Behavior {
	UltrasonicSensor sonar = new UltrasonicSensor(SensorPort.S1);
	private float distance = 255;

	private boolean _suppressed = false;
	private PilotAvoid pilot;

	public AvoidObstacle(PilotAvoid pilot) {
		this.pilot = pilot;
		this.isDaemon();
		this.start();
	}
	
	public void run() {
		while (true) {
			distance = sonar.getDistance();
		}
	}

	public int takeControl() {
		return (distance < 15) ? 20 : 0;
	}

	public void suppress() {
		_suppressed = true;// standard practice for suppress methods
	}

	public void action() {
		_suppressed = false;
		
		Sound.buzz();
		
		// Take evasive action.
		
		while (!_suppressed) {
			Thread.yield(); // don't exit till suppressed
		}
//		Motor.A.stop(true); // not strictly necessary, but good programming practice
//		Motor.C.stop(true);
	}
}
