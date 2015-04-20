import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.MotorPort;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;

public class Vehicle2c {
	static UltrasonicSensor portLeft = new UltrasonicSensor(SensorPort.S2);
	static UltrasonicSensor portRight = new UltrasonicSensor(SensorPort.S3);
	static MotorPort motorLeft = MotorPort.B;
	static MotorPort motorRight = MotorPort.C;
	static int rawLeft;
	static int rawRight;
	static double powerLeft = 75;
	static double powerRight = 75;
	
	public static void main(String[] args) throws InterruptedException {
		LCD.clear();		
		
		while(true) {
			rawLeft = portLeft.getDistance();
			rawRight = portRight.getDistance();						
			
			powerLeft = 0.625 * rawRight + 37.5;
			powerRight = 0.625 * rawLeft + 37.5;					
			
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
