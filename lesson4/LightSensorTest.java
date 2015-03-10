import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;


public class LightSensorTest {
	
	public static void main (String[] args) {
		BlackWhiteSensor sensor = new BlackWhiteSensor(SensorPort.S1);
	
		sensor.calibrate();		
		
		while (!Button.ESCAPE.isDown()) {
			LCD.drawString("White: " + sensor.white(), 0, 3);
			LCD.drawString("Black: " + sensor.black(), 0, 4);
			LCD.drawString("Light: ", 0, 5);
			LCD.drawInt(sensor.light(), 4, 10, 5);
		}
	}
}
