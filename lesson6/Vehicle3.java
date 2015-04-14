import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.MotorPort;
import lejos.nxt.SensorPort;
import lejos.nxt.SoundSensor;
import lejos.nxt.UltrasonicSensor;

public class Vehicle3 {
	static UltrasonicSensor portLeft = new UltrasonicSensor(SensorPort.S2);
	static UltrasonicSensor portRight = new UltrasonicSensor(SensorPort.S3);
	static SensorPort soundLeft = SensorPort.S1;
	static SensorPort soundRight = SensorPort.S4;
	static MotorPort motorLeft = MotorPort.B;
	static MotorPort motorRight = MotorPort.C;
	static int rawDistanceLeft;
	static int rawDistanceRight;
	static double powerLeft = 75;
	static double powerRight = 75;
	static int rawSoundLeft;
	static int rawSoundRight;
	
	public static void main(String[] args) throws InterruptedException {
		LCD.clear();	
		soundLeft.setTypeAndMode(SensorPort.TYPE_SOUND_DB,
				SensorPort.MODE_PCTFULLSCALE);
		soundRight.setTypeAndMode(SensorPort.TYPE_SOUND_DB,
				SensorPort.MODE_PCTFULLSCALE);
		
		while(true) {
			rawDistanceLeft = Math.min(100, portLeft.getDistance());
			rawDistanceRight = Math.min(100, portRight.getDistance());
			rawSoundLeft = soundLeft.readRawValue();
			rawSoundRight = soundRight.readRawValue();
			
			//powerLeft = 0.625 * rawDistanceRight + 37.5;
			//powerRight = 0.625 * rawDistanceLeft + 37.5;
			
			powerLeft = 25 + (25.0 / 100.0) * (double)rawDistanceRight;
			powerRight = 25 + (25.0 / 100.0) * (double)rawDistanceLeft;
			
			double soundPowerLeft = 25 + (25.0 / 1023.0) * (double)rawSoundLeft;
			double soundPowerRight = 25 + (25.0 / 1023.0) * (double)rawSoundRight;
			
			powerLeft += soundPowerLeft;
			powerRight += soundPowerRight;
			
			motorLeft.controlMotor((int)powerLeft, MotorPort.FORWARD);
			motorRight.controlMotor((int)powerRight, MotorPort.FORWARD);
			
			LCD.drawString("SoundLeft: ", 0, 0);
			LCD.drawInt((int)rawSoundLeft,  4,  0, 1);
			LCD.drawString("PowerLeft: ", 0, 2);
			LCD.drawInt((int)powerLeft,  4,  0, 3);
			LCD.drawString("SoundRight: ", 0, 4);
			LCD.drawInt((int)rawSoundRight,  4,  0, 5);
			LCD.drawString("PowerRight: ", 0, 6);
			LCD.drawInt((int)powerRight,  4,  0, 7);
		}
		
	}

}
