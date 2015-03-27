
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.GyroSensor;

/**
 * Test of Gyro Sensor
 * Records the minimum, maximum and current values
 * 
 * @author Lawrie Griffiths
 */
public class GyroTest 
{
	public static void main(String[] args) throws Exception
	{
		GyroSensor gyro = new GyroSensor(SensorPort.S2);
		float minValue = 1023, maxValue = 0;
		int sampleInterval = 5; // ms
		
		LCD.drawString("Get ready for calibrating.", 0, 0);
		Button.waitForAnyPress();
		
		Car.forward(50, 50);
		
		Thread.sleep(5000);
		
		LCD.clear();
		LCD.drawString("Calibrating. Don't move!", 0, 0);
		gyro.recalibrateOffset();
		
		LCD.clear();
		LCD.drawString("Done Calibrating.", 0, 0);
		Button.waitForAnyPress();
		
		LCD.clear();
		LCD.drawString("Gyro Test:", 0, 0);		
		LCD.drawString("Min:", 0, 2);
		LCD.drawString("Max:", 0, 3);
		LCD.drawString("Current:", 0, 4);
		
		while(!Button.ESCAPE.isDown()) 
		{
			float value = gyro.readValue();
			minValue = Math.min(minValue, value);
			maxValue = Math.max(maxValue, value);
			
			LCD.drawInt((int) minValue, 6, 5, 2);
			LCD.drawInt((int) maxValue, 6, 5, 3);
			LCD.drawInt((int)(value), 6, 9, 4);
			
			Thread.sleep(sampleInterval);
			
		}
	}
}
