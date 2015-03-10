import lejos.nxt.*;
/**
 * A simple line follower for the LEGO 9797 car with
 * a light sensor. Before the car is started on a line
 * a BlackWhiteSensor is calibrated to adapt to different
 * light conditions and colors.
 * 
 * The light sensor should be connected to port 3. The
 * left motor should be connected to port C and the right 
 * motor to port B.
 * 
 * @author  Ole Caprani
 * @version 20.02.13
 */
public class LineFollower
{
  public static void main (String[] aArg)
  throws Exception
  {
     final int power = 75;
	  
     ThreeColorSensor sensor = new ThreeColorSensor (SensorPort.S1);
	 
     sensor.calibrate();
     
     while (!Button.ENTER.isUp());
     while (!Button.ENTER.isDown());
	 
     LCD.clear();
     LCD.drawString("Light: ", 0, 2); 
     
     int greenCounter = 0;
	 
     while (! Button.ESCAPE.isDown())
     {
	     LCD.drawInt(sensor.light(),4,10,2);
	     LCD.refresh();
	     
	     if ( sensor.black() ) {
	    	 Car.forward(power, 50);
	    	 greenCounter = 0;
	     } else if ( sensor.white() ) {
	    	 Car.forward(50, power);
	    	 greenCounter = 0;
	     } else if ( sensor.green() ) {
	    	 Car.forward(power, power);
	    	 greenCounter++;
	     }
	    	 
	     if (greenCounter >= 50) {
	    	 Car.stop();
	     }
	     
	     if (Button.ENTER.isDown()) {
	    	 greenCounter = 0;
	     }
	     
	     Thread.sleep(10);
     }
     
     Car.stop();
     LCD.clear();
     LCD.drawString("Program stopped", 0, 0);
     LCD.refresh();
   }
}
