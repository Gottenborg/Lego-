import lejos.nxt.*;
import lejos.nxt.comm.*;
import lejos.util.Delay;

import java.io.*;

/**
 * Setup a Bluetooth connection to a PC that sends parameters to control
 * a Tracker
 * 
 * @author Ole Caprani
 * @version 27-1-15
 */
public class RCparamTracker
{    
    public static void main(String [] args)  
    {
        Tracker tracker = new Tracker();
        PCconnection pc = new PCconnection(tracker);
        
        pc.go();
        tracker.go();
        
        LCD.drawString("RCtracker", 0, 1);
       	   
        while (! Button.ESCAPE.isDown())
        {		   
            LCD.drawString("minPower: " + tracker.getMinPower() + "  ",0,3);
            LCD.drawString("Pgain: " + tracker.getPgain() + "  ",0,4);
            LCD.drawString("distance: " + tracker.getDistance() + "  ",0,5);
            LCD.drawString("power: " + tracker.getPower() + "  ",0,6);
            Delay.msDelay(100);
        }
   	 
        tracker.stop();
        pc.stop();
        LCD.clear();
        LCD.drawString("Program stopped", 0, 0);
        Delay.msDelay(2000);
    }

}

