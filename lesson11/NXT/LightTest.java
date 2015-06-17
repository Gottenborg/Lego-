import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;


public class LightTest {
	
	public static void main (String[] args) {
		LightSensor sensor = new LightSensor(SensorPort.S1);	
		
		while (!Button.ESCAPE.isDown()) {			
			LCD.drawString("Light: ", 0, 5);
			LCD.drawInt(sensor.readNormalizedValue(), 4, 10, 5);
		}
	}
}
