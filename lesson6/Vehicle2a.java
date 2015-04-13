import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.SensorPort;

public class Vehicle2a {
	static SensorPort portLeft = SensorPort.S2;
	static SensorPort portRight = SensorPort.S3;
	static MotorPort motorLeft = MotorPort.C;
	static MotorPort motorRight = MotorPort.B;
	static int rawLeft;
	static int rawRight;
	static double powerLeft;
	static double powerRight;
	
	public static void main(String[] args) throws InterruptedException {
		LCD.clear();
		
		while(true) {
			rawLeft = portLeft.readRawValue();
			rawRight = portRight.readRawValue();
			powerLeft = 100.0 - ((100.0 / 1023.0) * rawLeft) * 2;
			powerRight = 100.0 - ((100.0 / 1023.0) * rawRight) * 2;
			motorLeft.controlMotor((int)powerLeft, MotorPort.FORWARD);
			motorRight.controlMotor((int)powerRight, MotorPort.FORWARD);
			
			LCD.drawString("RawLeft: ", 0, 0);
			LCD.drawInt(rawLeft,  4,  0, 1);
			LCD.drawString("PowerLeft: ", 0, 2);
			LCD.drawInt((int)powerLeft,  4,  0, 3);
			LCD.drawString("RawRight: ", 0, 4);
			LCD.drawInt(rawRight,  4,  0, 5);
			LCD.drawString("PowerRight: ", 0, 6);
			LCD.drawInt((int)powerRight,  4,  0, 7);
		}
		
	}

}
