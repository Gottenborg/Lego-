import lejos.nxt.*;
/**
 * A simple sound sensor test program. 
 */
public class SoundSensorTest 
{

   public static void main(String [] args)  
   throws Exception 
   {
       SoundSensor ss = new SoundSensor(SensorPort.S2);
       
       LCD.drawString("Sound ", 0, 0);
       LCD.drawString("Sound lvl ", 0, 2);
	   
       while (! Button.ESCAPE.isDown())
       {
    	   LCD.drawInt(ss.readValue(),3,13,2);    	   
       }
       LCD.clear();
       LCD.drawString("Program stopped", 0, 0);
       Thread.sleep(2000);
   }
}
