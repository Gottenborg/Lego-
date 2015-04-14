import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.MotorPort;
import lejos.nxt.SensorPort;

public class Vehicle2b {
	static LightSensor portLeft = new LightSensor(SensorPort.S3);
	static LightSensor portRight = new LightSensor(SensorPort.S2);
	static MotorPort motorLeft = MotorPort.B;
	static MotorPort motorRight = MotorPort.C;
	static int rawLeft;
	static int rawRight;
	static double powerLeft;
	static double powerRight;
	
	public static void main(String[] args) throws InterruptedException {
		LCD.clear();
		portLeft.setFloodlight(false);
		portRight.setFloodlight(false);
		
		while(true) {
			rawLeft = portLeft.readNormalizedValue() - 139;
			rawRight = portRight.readNormalizedValue() - 139;
			powerLeft = 50.0 - ((50.0 / 745.0) * rawLeft) * 2;
			powerRight = 50.0 - ((50.0 / 745.0) * rawRight) * 2;	
			powerLeft = (powerLeft < 0) ? powerLeft - 50 : powerLeft + 50;
			powerRight = (powerRight < 0) ? powerRight - 50 : powerRight + 50;
			
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
