import lejos.nxt.*;
import lejos.nxt.comm.*;
import lejos.util.Delay;

import java.io.*;

/**
 * Receive data from a PC, a phone, or another Bluetooth device.
 * 
 * Waits for a Bluetooth connection, receives an integers and a float that are
 * interpreted as minPower and gain in a Tracker. 
 * 
 * Based on Lawrie Griffiths BTSend
 * 
 * @author Ole Caprani
 * @version 27-1-15
 */
public class PCconnection extends Thread
{
    private String connected = "BT Connected";
    private String waiting = "BT Waiting...";
    private String closing = "BT Closing...";

    private BTConnection btc;
    private DataInputStream dis;
    
    private boolean running;

    private Tracker tracker;
    private int minPower;
    private float Pgain;
	
    public PCconnection(Tracker tracker) 
    {				
        LCD.drawString(waiting,0,0);
        btc = Bluetooth.waitForConnection();
        
        LCD.clear();
        LCD.drawString(connected,0,0);	

        dis = btc.openDataInputStream();
        
        this.tracker = tracker;
    }
    
    private void close() 
    {
        LCD.clear();
        LCD.drawString(closing,0,0);
    	try 
    	{
    	    dis.close();
            Thread.sleep(100); // wait for data to drain
            btc.close();    	
    	}
        catch (Exception e)
        {}
    } 
    
	public void go()
    {
		running = true;
		this.start();
    }
	
	public void stop()
    {
	   	close();
		running = false;
    }
    
    public void run()
    {
    	while ( running )
    	{
            try 
            {
                minPower = dis.readInt();
                Pgain = dis.readFloat();               
                tracker.setMinPower(minPower);
                tracker.setPgain(Pgain);
                Delay.msDelay(100);
            }
            catch (Exception e)
            {}
    	}
    }
    
	public int getMinPower()
	{
		return minPower;
	}
	
	public float getPgain()
	{
		return Pgain;
	}
    
}

