import java.io.File;
import java.util.Vector;

import lejos.geom.Point;
import lejos.nxt.*;
import lejos.nxt.comm.RConsole;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.Pose;
import lejos.util.Delay;

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
	public OdometryPoseProvider poseProvider = new OdometryPoseProvider(pilot);
	
	public Point desPoint;

	public PilotAvoid(boolean usb, float desX, float desY) {
		PC = new SlaveIOStreams(usb);
		PC.open();
		
		// Send destination to PC.
		PC.output(desX);
		PC.output(desY);

		pilot.setTravelSpeed(travelSpeed);
		pilot.setRotateSpeed(rotateSpeed);
		
		Pose initialPose = new Pose(0,0,0);	      
	    poseProvider.setPose(initialPose);
		desPoint = new Point(desX, desY);			
	}

	public void sendMove(Move move) {
		PC.output((move.getMoveType() == Move.MoveType.TRAVEL ? 0 : 1));
		PC.output(move.getDistanceTraveled());
		PC.output(move.getAngleTurned());
	}

	public void travel(double distance, boolean immediateReturn) {
		pilot.travel(distance, immediateReturn);
	}

	public void rotate(double angle, boolean immediateReturn) {
		pilot.rotate(angle, immediateReturn);		
	}	

	public static void main(String[] args) {
		PilotAvoid pilot = new PilotAvoid(false, 1000, 500);

		LCD.clear();
		LCD.drawString("PilotAvoid", 0, 0);
		
		Sound.beep();
		while (!Button.ENTER.isDown())
			Thread.yield();
		Sound.twoBeeps();
		
		Behavior b1 = new FindGoal(pilot);
		Behavior b2 = new AvoidObstacle(pilot);
		Behavior b3 = new RecognizeGoal(pilot);	
		Behavior[] behaviorList = { b1, b2, b3};
		Arbitrator arbitrator = new Arbitrator(behaviorList);
		arbitrator.start();
	}	
}

class FindGoal implements Behavior {	
	private boolean _suppressed = false;
	private PilotAvoid pilot;

	public FindGoal(PilotAvoid pilot) {
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
		Sound.beepSequence();
		Pose p = pilot.poseProvider.getPose();
		
		// Calculate angle based on destination.
		float angle = p.angleTo(pilot.desPoint);
//		float degrees = (angle > 0) ? angle - p.getHeading() : angle + p.getHeading();
		float degrees = - p.getHeading() + angle;
		
//		if (angle < 0 && p.getHeading() < 0) {
//			degrees = angle - p.getHeading();
//		} else if (angle > 0 && p.getHeading() < 0) {
//			degrees = angle + p.getHeading();
//		} else if (angle > 0 && p.getHeading() > 0) {
//			degrees = angle + p.getHeading();
//		} else if (angle < 0 && p.getHeading() > 0) {
//			degrees = angle - p.getHeading(); // done
//		}
		
		pilot.rotate(degrees, false);							
		pilot.sendMove(pilot.pilot.getMovement());
		
		pilot.travel(p.distanceTo(pilot.desPoint), true);		
		while (!_suppressed && Motor.A.isMoving()) {			
			Thread.yield();
		}
		pilot.sendMove(pilot.pilot.getMovement());		
		
		Motor.A.stop(true); // not strictly necessary, but good programming practice
		Motor.C.stop(true);				
	}	
}

class AvoidObstacle extends Thread implements Behavior {
	UltrasonicSensor sonar = new UltrasonicSensor(SensorPort.S2);
	private int distance = 255;
	private int count = 0;
	private boolean active = false;

	private boolean _suppressed = false;
	private PilotAvoid pilot;

	public AvoidObstacle(PilotAvoid pilot) {
		this.pilot = pilot;
		this.setDaemon(true);
		this.start();
	}
	
	public void run() {
		while (true) {
			distance = sonar.getDistance();
			LCD.drawInt(distance, 3, 0, 2);
		}
	}

	public int takeControl() {
		count++;
		int t = (distance < 25 || active) ? 20 : 0;
		LCD.drawInt(t, 2, 0, 3);
		LCD.drawInt(count, 6, 0, 4);
		return t;
	}

	public void suppress() {
		_suppressed = true;// standard practice for suppress methods
	}

	public void action() {
		_suppressed = false;
		active = true;
		Sound.buzz();
		
		// Look for a clear passage.		
		pilot.rotate(45, true);
		while (!_suppressed && Motor.A.isMoving()) {			
			Thread.yield();
		}
		pilot.sendMove(pilot.pilot.getMovement());
		float disLeft = sonar.getDistance() * 10;		
		
		pilot.rotate(-90, true);
		while (!_suppressed && Motor.A.isMoving()) {			
			Thread.yield();
		}
		pilot.sendMove(pilot.pilot.getMovement());
		float disRight = sonar.getDistance() * 10;
		
		// Take evasive action.
		if (disLeft > disRight) {
			pilot.rotate(90, true);
			while (!_suppressed && Motor.A.isMoving()) {			
				Thread.yield();
			}
			pilot.sendMove(pilot.pilot.getMovement());
		} 
		
		pilot.travel(Math.min(Math.max(disLeft - 200, disRight - 200), 300), true);
		
		while (!_suppressed && Motor.A.isMoving()) {			
			Thread.yield();
		}
		pilot.sendMove(pilot.pilot.getMovement());
		
		Motor.A.stop(true); // not strictly necessary, but good programming practice
		Motor.C.stop(true);
		active = false;
	}
}

class RecognizeGoal implements Behavior {	
	private boolean _suppressed = false;
	private PilotAvoid pilot;
	private Pose p;

	public RecognizeGoal(PilotAvoid pilot) {
		this.pilot = pilot;
		
	}

	public int takeControl() {
		// Check if we reached the goal.
		p = pilot.poseProvider.getPose();
		float d = p.distanceTo(pilot.desPoint);
		LCD.drawInt((int)d, 5, 0, 5);
		return (d <= 10) ? 50 : 0; 
	}

	public void suppress() {
		_suppressed = true;// standard practice for suppress methods
	}

	public void action() {
		_suppressed = false;
		// We reached the target!
		Sound.beepSequenceUp();
		while (!Button.ESCAPE.isDown()) {
			Delay.msDelay(100);
		}
		System.exit(0);
	}	
}