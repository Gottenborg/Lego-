
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;

/**
 * Test of raw values from a light sensor connected to SensorPort S3
 * 
 * @author Ole Caprani
 * @version 17.01.15
 */
public class SensorPortTest 
{
	public static void main(String[] args)
	throws Exception
	{
		SensorPort port = SensorPort.S3;
		
		// Initialize the sensor port with the TYPE and MODE
		// of the sensor connected to the port otherwise 
		// readRawValue will return -1. 
		port.setTypeAndMode(SensorPort.TYPE_LIGHT_ACTIVE, SensorPort.MODE_RAW);
		
		LCD.drawString("SensorPort Test:", 0, 0);
		
		while(!Button.ESCAPE.isDown()) 
		{			
			LCD.drawString("Raw: " + port.readRawValue(), 0, 2);
		}
		
		LCD.clear();
		LCD.drawString("Program stopped", 0, 0);
		Thread.sleep(2000);
	}
}