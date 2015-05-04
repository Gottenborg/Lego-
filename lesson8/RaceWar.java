import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.MotorPort;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;


public class RaceWar {

	static LightSensor left = new LightSensor(SensorPort.S1);
	static LightSensor right = new LightSensor(SensorPort.S4);
	static MotorPort motorLeft = MotorPort.B;
	static MotorPort motorRight = MotorPort.C;

	static int blackTreshold;
	static int whiteTreshold;
	static int blackWhiteTreshold;
	
	static int command = 0; // 0 = left, 1 = right, 2 = u-turn, 3 = finish
	static int power = 70;
	
	public static void main(String[] args) throws InterruptedException {
		calibrate();
		
		 LCD.clear();
		 while( !Button.ENTER.isDown() ){
			 LCD.drawString("Start", 0, 0);
		 }
		 
		 motorRight.controlMotor(power, MotorPort.BACKWARD);
		 motorLeft.controlMotor(power - 5, MotorPort.BACKWARD);
		 int times = 0;
		 
		 while(true) {
			 LCD.drawInt(command, 0, 0);
			 if(command == 0) {
				 if(right.readValue() < blackWhiteTreshold) {
					 motorRight.controlMotor(power, MotorPort.FORWARD);
					 motorLeft.controlMotor(power, MotorPort.BACKWARD);
				 } else {
					 motorRight.controlMotor(power, MotorPort.BACKWARD);
					 motorLeft.controlMotor(power - 5, MotorPort.BACKWARD);
				 }
				 if(left.readValue() < blackWhiteTreshold) {
					 times++;
				 }
				 if(times > 600) {
					 times = 0;
					 command = 1;
					 Sound.beep();
				 }
			 } else if(command == 1) {
				 if(left.readValue() < blackWhiteTreshold) {
					 motorLeft.controlMotor(power, MotorPort.FORWARD);
					 motorRight.controlMotor(power, MotorPort.BACKWARD);
				 } else {
					 motorLeft.controlMotor(power, MotorPort.BACKWARD);
					 motorRight.controlMotor(power - 5, MotorPort.BACKWARD);
				 }
				 if(right.readValue() < blackTreshold) {
					 times++;
				 }
				 if(times > 400) {
					 times = 0;
					 command = 2;
					 Sound.beep();
				 }
			 } else if(command == 2) {
				 motorLeft.resetTachoCount();
				 motorRight.resetTachoCount();
				 while(motorRight.getTachoCount() * -1 < 360) {
					 LCD.drawInt(motorRight.getTachoCount(), 0, 1);
					 motorLeft.controlMotor(power, MotorPort.BACKWARD);
					 motorRight.controlMotor(power, MotorPort.BACKWARD);
				 }
				 motorRight.resetTachoCount();
				 while(motorRight.getTachoCount() * -1 < 180) {
					 motorLeft.controlMotor(power, MotorPort.FORWARD);
					 motorRight.controlMotor(power, MotorPort.BACKWARD);
				 }
				 
				 motorRight.resetTachoCount();
				 while(motorRight.getTachoCount() * -1 < 360) {
					 motorLeft.controlMotor(power, MotorPort.BACKWARD);
					 motorRight.controlMotor(power, MotorPort.BACKWARD);
				 }

				 command = 3;
				 Sound.beep();
			 } else if(command == 3) {
				 power = 40;
				 if(right.readValue() > blackWhiteTreshold) {
					 motorLeft.controlMotor(power-20, MotorPort.BACKWARD);
					 motorRight.controlMotor(power, MotorPort.BACKWARD);
				 } else {
					 motorRight.controlMotor(power, MotorPort.FORWARD);
					 motorLeft.controlMotor(power, MotorPort.BACKWARD);
				 }
				 
			 } 
		 }
	}

	public static void calibrate() {
		// black
		int leftV;
		int rightV;

		rightV = right.readValue();
		while( !Button.ENTER.isDown() ){
			leftV = left.readValue();
			rightV = right.readValue();
			blackTreshold = (leftV + rightV) / 2;
			LCD.drawString("Calibrate black", 0, 0);
			LCD.drawInt(blackTreshold, 4, 10, 2);
		}
		while( Button.ENTER.isDown() ){}

		// white
		while( !Button.ENTER.isDown() ){
			leftV = left.readValue();
			rightV = right.readValue();
			whiteTreshold = (leftV + rightV) / 2;
			LCD.drawString("Calibrate white", 0, 0);
			LCD.drawInt(whiteTreshold, 4, 10, 2);
		}
		while( Button.ENTER.isDown() ){}
		blackWhiteTreshold = (blackTreshold + whiteTreshold) / 2;
	}

}
