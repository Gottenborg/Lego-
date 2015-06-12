import lejos.nxt.*;
import lejos.nxt.comm.RConsole;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.Pose;
import lejos.util.Delay;

public class ArenaRobot {
	double mult = 1.007;
	double leftWheelDiameter = 55 * mult, rightWheelDiameter = 54.84 * mult,
			trackWidth = 165;
	double travelSpeed = 100, rotateSpeed = 100;
	NXTRegulatedMotor left = Motor.A;
	NXTRegulatedMotor right = Motor.C;	
	DifferentialPilot pilot = new DifferentialPilot(leftWheelDiameter,
			rightWheelDiameter, trackWidth, left, right, false);
	OdometryPoseProvider poseProvider = new OdometryPoseProvider(pilot);

	public ArenaRobot() {				
		pilot.setTravelSpeed(travelSpeed);
		pilot.setRotateSpeed(rotateSpeed);		
		
		Pose initialPose = new Pose(0,0,0);
		RConsole.open();		
		poseProvider.setPose(initialPose);
	}

	public void travel(double distance) {
		pilot.travel(distance, true);
	}

	public void rotate(double angle) {
		pilot.rotate(angle, true);
	}

	public void show(Pose p)	{
		LCD.clear();
		LCD.drawString("Pose X " + p.getX(), 0, 2);
		LCD.drawString("Pose Y " + p.getY(), 0, 3);
		LCD.drawString("Pose V " + p.getHeading(), 0, 4);
	}

	public static void main(String[] args) {
		ArenaRobot pilot = new ArenaRobot();

		LCD.clear();
		LCD.drawString("ArenaRobot", 0, 0);

		Sound.beep();
		while (!Button.ENTER.isDown())
			Thread.yield();
		Sound.twoBeeps();

		Behavior b1 = new Wander(pilot);
		Behavior b2 = new AvoidEdge(pilot);
		Behavior b3 = new Exit(pilot);		
		Behavior[] behaviorList = {b1, b2, b3};
		Arbitrator arbitrator = new Arbitrator(behaviorList);
		arbitrator.start();
	}	
}

class Wander implements Behavior {
	private static final int TRAVEL = 0;
	private static final int ROTATE = 1;	

	private boolean _suppressed = false;
	private ArenaRobot pilot;

	public Wander(ArenaRobot pilot) {
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

		double rand = Math.random(); 
		if (rand > 0.5) {
			pilot.travel(rand * 2000);
		} else {
			pilot.rotate((Math.random() > 0.5) ? rand * 180 : rand * -180);
		}	

		while (!_suppressed && Motor.A.isMoving()) {
			Thread.yield(); // don't exit till suppressed
		}
		
		pilot.show(pilot.poseProvider.getPose());

		Motor.A.stop(true); // not strictly necessary, but good programming practice
		Motor.C.stop(true);		
	}	
}

class AvoidEdge extends Thread implements Behavior {
	LightSensor light = new LightSensor(SensorPort.S1);
	TouchSensor touch = new TouchSensor(SensorPort.S2);		

	private int lightVal = 100;
	private boolean _suppressed = false;
	private ArenaRobot pilot;

	public AvoidEdge(ArenaRobot pilot) {
		this.pilot = pilot;
		this.isDaemon();
		this.start();
	}

	public void run() {
		while (true) {
			lightVal = light.getLightValue();
		}
	}

	public int takeControl() {
		return (lightVal < 40 || touch.isPressed()) ? 20 : 0;
	}

	public void suppress() {
		_suppressed = true;// standard practice for suppress methods
	}

	public void action() {
		_suppressed = false;

		Sound.buzz();

		// Take evasive action.
		pilot.travel(-200);
		while (!_suppressed && Motor.A.isMoving()) {
			Thread.yield(); // don't exit till suppressed
		}
		
		pilot.show(pilot.poseProvider.getPose());
		
		double rand = Math.random();
		double angle = (rand > 0.5) ? 90 + (90 * rand) : -90 - (90 * rand);		
		pilot.rotate(angle);

		while (!_suppressed && Motor.A.isMoving()) {
			Thread.yield(); // don't exit till suppressed
		}
		
		pilot.show(pilot.poseProvider.getPose());

		Motor.A.stop(true); // not strictly necessary, but good programming practice
		Motor.C.stop(true);
	}
}

class Exit implements Behavior {
	private boolean _suppressed = false;
	private ArenaRobot pilot;

	public Exit(ArenaRobot pilot) {
		this.pilot = pilot;
	}

	public int takeControl() {
		return (Button.ESCAPE.isDown()) ? 100 : 0 ; // this behavior always wants control.
	}

	public void suppress() {
		_suppressed = true;// standard practice for suppress methods
	}

	public void action() {
		_suppressed = false;
		
		pilot.show(pilot.poseProvider.getPose());

		Motor.A.stop(true); // not strictly necessary, but good programming practice
		Motor.C.stop(true);
		
		Delay.msDelay(2000);
		
		System.exit(0);
	}	
}