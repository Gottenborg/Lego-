import java.util.Random;

import lejos.nxt.*;
import lejos.util.Delay;

/*
 * Escape behavior
 */

public class Escape extends Thread {
	private SharedCar car = new SharedCar();

	private int power = 70, ms = 500;
	TouchSensor left = new TouchSensor(SensorPort.S1);
	TouchSensor right = new TouchSensor(SensorPort.S4);
	Random r = new Random();
	
	public Escape(SharedCar car) {
		this.car = car;
	}
	
	public void run() 
    {				       
        while (true)
        {
	    	// Front bump
	    	if(left.isPressed() && right.isPressed()) {
	    		car.backward(power, power);
	    		Delay.msDelay(ms);
	    		// Either turn right or left (50/50)
	    		if(r.nextBoolean()) {
	    			car.forward(0, power);
	    			Delay.msDelay(ms);
	    		} else {
	    			car.forward(power, 0);
	    			Delay.msDelay(ms);
	    		}
	    	}
	    	// Left bump
	    	else if(left.isPressed()) {
	    		car.backward(power, power);
	    		Delay.msDelay(ms);
    			car.forward(power, 0);
    			Delay.msDelay(ms);
	    	}
	    	// Right bump
	    	else if(right.isPressed()) {
	    		car.backward(power, power);
	    		Delay.msDelay(ms);
    			car.forward(0, power);
    			Delay.msDelay(ms);
	    	} else {
	    		car.noCommand();
	    	}
        }
    }

}
