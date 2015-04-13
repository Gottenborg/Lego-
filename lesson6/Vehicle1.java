import lejos.nxt.*;

public class Vehicle1 {
	static SensorPort port = SensorPort.S2;
	static MotorPort motorLeft = MotorPort.C;
	static MotorPort motorRight = MotorPort.B;
	static int raw;
	static double power;
	
	public static void main(String[] args) throws InterruptedException {
		LCD.clear();
		
		while(true) {
			raw = port.readRawValue();
//			Map 0 to 100
//			power = 100.0 - ((100.0 / 1023.0) * raw);
//			Map -100 to 100
			power = 100.0 - ((100.0 / 1023.0) * raw) * 2;
			motorLeft.controlMotor((int)power, MotorPort.FORWARD);
			motorRight.controlMotor((int)power, MotorPort.FORWARD);
			
			LCD.drawString("Raw: ", 0, 0);
			LCD.drawInt(raw,  4,  0, 1);
			LCD.drawString("Power: ", 0, 2);
			LCD.drawInt((int)power,  4,  0, 3);
		}
		
	}

}
