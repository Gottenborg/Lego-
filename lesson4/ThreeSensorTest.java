import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;


public class ThreeSensorTest {
	
	public static void main (String[] args) {
		ThreeColorSensor sensor = new ThreeColorSensor(SensorPort.S1);
	
		sensor.calibrate();		
		
		while (!Button.ESCAPE.isDown()) {
			LCD.drawString("White: " + sensor.white(), 0, 3);
			LCD.drawString("Black: " + sensor.black(), 0, 4);
			LCD.drawString("Green: " + sensor.green(), 0, 5);
			LCD.drawString("Light: ", 0, 6);
			LCD.drawInt(sensor.light(), 4, 10, 6);
		}
	}
}
