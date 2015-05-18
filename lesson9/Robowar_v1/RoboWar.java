import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;

public class RoboWar {

	public static void main(String[] args) {
		Motor.A.setSpeed(800);
		Motor.B.setSpeed(2000);
		Motor.C.setSpeed(800);

		lejos.robotics.subsumption.Behavior b1 = new Spin();
		lejos.robotics.subsumption.Behavior b2 = new AvoidWhiteLine();
		lejos.robotics.subsumption.Behavior b3 = new Attack();
		lejos.robotics.subsumption.Behavior b4 = new Exit();

		lejos.robotics.subsumption.Behavior[] behaviorList = { b1, b3, b2, b4 };
		lejos.robotics.subsumption.Arbitrator arb = new lejos.robotics.subsumption.Arbitrator(behaviorList);

		LCD.drawString("Start War !", 0, 3);
		
		Button.waitForAnyPress();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Motor.B.backward();
		arb.start();
	}
}

class Spin implements lejos.robotics.subsumption.Behavior {
	private boolean _suppressed = false;

	@Override
	public boolean takeControl() {
		return true;
	}

	@Override
	public void action() {
		LCD.drawString("Spin", 0, 3);
		_suppressed = false;
		Motor.A.setSpeed(300);
		Motor.C.setSpeed(300);
		
		Motor.A.forward();
		Motor.C.backward();

		while (!_suppressed) {
			Thread.yield(); // don't exit till suppressed
		}
		
		Motor.A.stop();
		Motor.C.stop();
		Motor.A.setSpeed(800);
		Motor.C.setSpeed(800);
	}

	@Override
	public void suppress() {
		_suppressed = true;
	}

}

class AvoidWhiteLine implements lejos.robotics.subsumption.Behavior {
	private LightSensor rightLight = new LightSensor(SensorPort.S3);
	private LightSensor leftLight = new LightSensor(SensorPort.S2);

	private boolean _suppressed = false;

	@Override
	public boolean takeControl() {
		return rightLight.readValue() > 40 || leftLight.readValue() > 40;
	}

	@Override
	public void action() {
		_suppressed = false;

		// Backward for 1000 msec
		LCD.drawString("Drive backward", 0, 3);
		Motor.A.backward();
		Motor.C.backward();
		long now = System.currentTimeMillis();
		while (!_suppressed && (System.currentTimeMillis() < now + 500)) {
			Thread.yield(); // don't exit till suppressed
		}		
	}

	@Override
	public void suppress() {
		_suppressed = true;
	}
}

class Attack extends Thread implements lejos.robotics.subsumption.Behavior {
	UltrasonicSensor sonar = new UltrasonicSensor(SensorPort.S1);
	private int distance = 255;
	private boolean _suppressed = false;

	private Thread sonicThread = new Thread() {
		public void run() {
			while (true) {
				distance = sonar.getDistance();
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};
	
	public Attack() {
		sonicThread.start();
	}

	@Override
	public boolean takeControl() {
		LCD.drawInt(distance, 3, 0, 1);
		return distance < 60;
	}

	@Override
	public void action() {
		LCD.drawString("Drive forward", 0, 3);
		
		_suppressed = false;
		Motor.A.forward();
		Motor.C.forward();

		while (!_suppressed) {
			Thread.yield(); // don't exit till suppressed
		}

		Motor.A.stop();
		Motor.C.stop();
	}

	@Override
	public void suppress() {
		_suppressed = true;
	}

}

class Exit implements lejos.robotics.subsumption.Behavior {
	private boolean _suppressed = false;

	public boolean takeControl() {
		return Button.ESCAPE.isDown();
	}

	public void suppress() {
		_suppressed = true;// standard practice for suppress methods
	}

	public void action() {
		System.exit(0);
	}
}
