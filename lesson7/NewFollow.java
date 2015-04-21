
import lejos.nxt.*;
import lejos.util.Delay;
/*
 * Follow behavior , inspired by p. 304 in
 * Jones, Flynn, and Seiger: 
 * "Mobile Robots, Inspiration to Implementation", 
 * Second Edition, 1999.
 */

class NewFollow extends Thread
{
    private SharedCar car = new SharedCar();

	private int power = 70, ms = 500, rotorPower = 30, rotorMs = 200;
	LightSensor light = new LightSensor(SensorPort.S3);
	MotorPort rotator = MotorPort.A;
	
	int frontLight, leftLight, rightLight, delta;
	int lightThreshold;
	
    public NewFollow(SharedCar car)
    {
       this.car = car;	
       lightThreshold = light.getLightValue();
    }
    
	public void run() 
    {				       
        while (true)
        {
	    	// Monitor the light in front of the car and start to follow
	    	// the light if light level is above the threshold
        	frontLight = light.getLightValue();
	    	while ( frontLight <= lightThreshold )
	    	{
	    		car.noCommand();
	    		frontLight = light.getLightValue();
	    	}
	    	
	    	// Follow light as long as the light level is above the threshold
	    	while ( frontLight > lightThreshold )
	    	{
	    		// Stop the car while reading values.
	    		car.stop();
	    		
	    		// Get the light to the left
	    		rotator.controlMotor(power, MotorPort.FORWARD);
	    		Delay.msDelay(rotorMs);
	    		leftLight = light.getLightValue();
	    		
	    		// Get the light to the right
	    		rotator.controlMotor(power, MotorPort.BACKWARD);	    		
	    		Delay.msDelay(rotorMs*2);	    		
	    		rightLight = light.getLightValue();
	    		
	    		// Turn back to start position
	    		rotator.controlMotor(power, MotorPort.FORWARD);
	    		Delay.msDelay(rotorMs);
	    		rotator.controlMotor(0, MotorPort.STOP);
	    	
	    		// Follow light for a while
	    		delta = leftLight-rightLight;
	    		car.forward(power-delta, power+delta);
	    		Delay.msDelay(ms);
    		
	    		frontLight = light.getLightValue();
	    	}
	    	
	    	car.stop();
	    	Delay.msDelay(ms);
 			
        }
    }
}

