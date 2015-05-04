import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;

public class RampClimber2 {

	public static void main(String[] args) throws Exception {
		final int power = 80;
		int MAXIMUM_POWER = 100;

		LightSensor lightSensorRight = new LightSensor(SensorPort.S4);
		LightSensor lightSensorLeft = new LightSensor(SensorPort.S1);

		int blackValue = 397;
		int whiteValue = 593;
		int edgeLight = ((blackValue + whiteValue) / 2);
		
		LCD.clear();
		LCD.drawString("Right light: ", 0, 0);
		LCD.drawString("Left light: ", 0, 1);
		LCD.drawString("Direction: ", 0, 2);
		
		Button.waitForAnyPress();
		
		Car.backward(96, 100);
		Thread.sleep(3100);
		Car.backward(90, 0);
		Thread.sleep(1500);
		//Car.backward(96, 100);
		//Thread.sleep(3500);
		//Car.backward(0, 100);
		//Thread.sleep(1000);
		
		
		Car.stop();
		Button.waitForAnyPress();

		/*
		while (!Button.ESCAPE.isDown()) {
			int lightRight = lightSensorRight.readNormalizedValue();
			int lightLeft = lightSensorLeft.readNormalizedValue() + 45;
		
			
			
			
			LCD.drawInt(lightRight, 4, 10, 0);
			LCD.drawInt(lightLeft, 4, 10, 1);
			LCD.drawInt(direction, 4, 10, 2);
			LCD.refresh();
			//Thread.sleep();
		}
		*/

		Car.stop();
		LCD.clear();
		LCD.drawString("Program stopped", 0, 0);
		LCD.refresh();
	}
}