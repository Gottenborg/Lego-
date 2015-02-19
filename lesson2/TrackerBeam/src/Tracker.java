import lejos.nxt.*;
import lejos.util.Delay;
/**
 * A LEGO 9797 car with a sonar sensor. The sonar is used
 * to maintain the car at a constant distance 
 * to objects in front of the car.
 * 
 * The sonar sensor should be connected to port 1. The
 * left motor should be connected to port C and the right 
 * motor to port B.
 * 
 * @author  Ole Caprani
 * @version 24.08.08
 */
public class Tracker extends Thread
{
	
    private boolean running;
    private int sampleInterval = 100; // ms default value
    private UltrasonicSensor us = new UltrasonicSensor(SensorPort.S1);
    private final int  noObject = 255;
    private int distance, desiredDistance = 35, // cm, default value
            power, minPower = 50, maxPower = 100; // default values
    private float error, Pgain = 2.0f; // default value
    
    public Tracker()
    {
    }
	
    public void go()
    {
        running = true;
        this.start();
    }
	
    public void stop()
    {
        running = false;
    }

    public void run()
    {
        while ( running )
        {		   
            distance = us.getDistance();
		 
            if ( distance != noObject ) 
            {
                error = distance - desiredDistance;
                power = (int)(Pgain * error);
                if ( error > 0 )
                { 
                    power = Math.min(minPower + power, maxPower);
                    Car.forward(power,power);
                }
                else 
                {
                    power = Math.min(minPower + Math.abs(power), maxPower);
                    Car.backward(power, power);		    	 
                }
             
                Delay.msDelay(sampleInterval);
            }			
        }
    }
	
    public int getDistance()
    {
        return distance;
    }
	
    public int getPower()
    {
        return power;
    }
	
    public int getMinPower()
    {
        return minPower;
    }	
	
    public float getPgain()
    {
        return Pgain;
    }
	
    public void setMinPower(int minPower)
    {
        this.minPower = minPower;
    }	
	
    public void setPgain(float Pgain)
    {
        this.Pgain = Pgain;
    }
}
