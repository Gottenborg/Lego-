import lejos.nxt.*;
public class PartyFinder {
	static final double turnDegree = 0.08797653958;

	static final long sampleInterval = 25;

	static MotorPort leftMotor  = MotorPort.C;
	static MotorPort rightMotor = MotorPort.B;

	static ADSensorPort soundSensorLeft = SensorPort.S2;
	static ADSensorPort soundSensorRight = SensorPort.S3;

	static int leftValue;
	static int rightValue;
	static int diff;

	public static void main(String[] args) throws Exception {
		soundSensorLeft.setTypeAndMode(SoundSensor.TYPE_SOUND_DB, SoundSensor.MODE_PCTFULLSCALE);
		soundSensorRight.setTypeAndMode(SoundSensor.TYPE_SOUND_DB, SoundSensor.MODE_PCTFULLSCALE);
		LCD.clear();
		LCD.drawString("Finding the party", 0, 0);
		while (!Button.ESCAPE.isDown()) {
			leftValue = soundSensorLeft.readRawValue();
			rightValue = soundSensorRight.readRawValue();

			diff = leftValue - rightValue;

			if(diff < 0) {
				// Party is to right
				right(70);
			} 
			else if(diff > 0) {
				// Party is to left
				left(70);
			}
			else {
				// Party is straight ahead ( or behind you :( )
				forward(70);
			}

			// Debug info
			LCD.drawInt(leftValue,4,0,1);
			LCD.drawInt(rightValue,4,0,2);
			LCD.drawInt(diff,4,0,3);

			Thread.sleep(sampleInterval);
		}
	}

	public static void stop() {
		leftMotor.controlMotor(0, MotorPort.STOP);
		rightMotor.controlMotor(0, MotorPort.STOP);
	}

	public static void forward( int power)
	{	
		leftMotor.controlMotor(power, MotorPort.FORWARD);
		rightMotor.controlMotor(power, MotorPort.FORWARD);	
	}
	public static void left( int power)
	{	
		leftMotor.controlMotor(power, MotorPort.FORWARD);
		rightMotor.controlMotor(power / 3, MotorPort.FORWARD);	
	}
	public static void right( int power)
	{	
		leftMotor.controlMotor(power / 3, MotorPort.FORWARD);
		rightMotor.controlMotor(power, MotorPort.FORWARD);	
	}
}
