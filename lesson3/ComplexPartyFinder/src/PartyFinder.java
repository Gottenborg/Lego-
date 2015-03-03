import java.util.ArrayList;

import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.util.Delay;


public class PartyFinder extends Thread {	
	private boolean running;
	private int sampleInterval = 30; // ms default value
	private final int averageCount = 20;
	private SensorPort sensorLeft = SensorPort.S2;
	private SensorPort sensorRight = SensorPort.S3;	

	private int powerLeft, powerRight, soundLeft, soundRight, diff,
		motorMode = Car.stop, diffAverage, soundLeftAvg, soundRightAvg;
	
	private ArrayList<Integer> diffList = new ArrayList<Integer>();
	private ArrayList<Integer> soundLeftList = new ArrayList<Integer>(); 
	private ArrayList<Integer> soundRightList = new ArrayList<Integer>(); 

	public PartyFinder() {
		sensorLeft.setTypeAndMode(SensorPort.TYPE_SOUND_DB,
				SensorPort.MODE_PCTFULLSCALE);
		sensorRight.setTypeAndMode(SensorPort.TYPE_SOUND_DB,
				SensorPort.MODE_PCTFULLSCALE);
	}

	public void go() {
		running = true;
		this.start();
	}

	public void stop() {
		running = false;
	}

	public void run() {
		// Do initial setup before main loop.
		int counter = 0;

		while ( running ) {		   
			// Read values from the sensors.
			// More noise from motors behind the robot.
			soundLeft = sensorLeft.readRawValue() + 100;
			// Less noise in front.
			soundRight = sensorRight.readRawValue();
			
			soundLeftList.add(soundLeft);
			soundRightList.add(soundRight);
			if (soundLeftList.size() > averageCount) soundLeftList.remove(0);
			if (soundRightList.size() > averageCount) soundRightList.remove(0);
			
			diff = soundLeft - soundRight;
			
			diffList.add(diff);
			if (diffList.size() > averageCount) diffList.remove(0);
			
			counter++;
			if (counter < averageCount) continue;
						
			diffAverage = getAverage(diffList);
			soundLeftAvg = getAverage(soundLeftList);
			soundRightAvg = getAverage(soundRightList);			
			powerLeft = 75;
			powerRight = 75;
			
			/*
			 * * Sensors:
			 * left = points backwards
			 * right = points forwards
			 * 
			 * * Steps:
			 * 1. Read values from sensors
			 * 2. Compare sensor values to determine direction
			 * 3. Move forwards/backwards towards loud sound
			 * 4. Adjust angle based on difference between values. 
			 * When forward:
			 * highest diff is straight ahead
			 * nearing zero diff when left of robot
			 * lower diff when right of robot
			 * When backwards
			 * lower diff is straight back (-60) 
			 * lowest diff is right of robot (0)
			 * highest diff is left of robot (-150)
			 * 5. Save average of highest sound readings to
			 * know if we are getting closer.
			 * 6. When average doens't get lower we are at the party.
			 */												
			
			motorMode = (soundLeftAvg > soundRightAvg) ? Car.forward : Car.backward;						
				
			// Adjust angle based on difference between values.
			if (motorMode == Car.forward) {
				if (diffAverage < 20) { // move left
					powerLeft = 60;
				} else if (diffAverage < 40) { // move right
					powerRight = 60;
				} else { // move straight
					
				}
				
			} else if (motorMode == Car.backward) {
				if (diffAverage > -20) { // move right
					powerRight = 60;
				} else if (diffAverage < 100) { // move left
					powerLeft = 60;
				} else { // move straight
					
				}
			}			

			Car.controlLeft(powerLeft, motorMode);
			Car.controlRight(powerRight, motorMode);
			
			LCD.drawInt(diffAverage, 4, 10, 2);
			LCD.drawInt(diff, 4, 10, 3);
			
			Delay.msDelay(sampleInterval);
		}
	}		
	
	private int getAverage(ArrayList<Integer> values) {
		float sum = 0;
		for (Integer i : values) {
			sum += i;
		}
		return (int) (sum / (float)values.size());
	}
	
	public int getLeftSound()
    {
        return soundLeftAvg;
    }
	
	public int getRightSound()
    {
        return soundRightAvg;
    }
}