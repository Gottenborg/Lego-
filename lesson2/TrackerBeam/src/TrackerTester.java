import lejos.nxt.*;
import lejos.util.Delay;
/**
 * A simple main program to test the Tracker class.
 * 
 * @author  Ole Caprani
 * @version 02.02.15
 */
public class TrackerTester
{
  public static void main (String[] aArg)
  {
     int distance, power;
     Tracker tracker = new Tracker();
     
     LCD.drawString("Tracker", 0, 0);
     LCD.drawString("Distance: ", 0, 3);
     LCD.drawString("Power:    ", 0, 4);
     
     tracker.go();
     	   
     while (! Button.ESCAPE.isDown())
     {		   
         distance = tracker.getDistance();
         power = tracker.getPower();

         LCD.drawInt(distance,4,10,3);
         LCD.drawInt(power, 4,10,4);
		 		 
         Delay.msDelay(100);
     }
	 
     tracker.stop();
     LCD.clear();
     LCD.drawString("Program stopped", 0, 0);
     Delay.msDelay(2000);
   }
}
