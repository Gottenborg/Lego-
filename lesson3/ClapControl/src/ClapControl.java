import lejos.nxt.*;

public class ClapControl {
	static ADSensorPort soundSensor = SensorPort.S3;
	static int soundThreshold = 85;
	static int clapCount = 0;

	private static void waitForClap() throws Exception {
		int soundLevel;
		double timerStart;
		double timerStop;
		double diff;

		do
		{
			do
			{
				soundLevel = soundSensor.readValue();
				LCD.drawInt(soundLevel,4,10,0); 
			}
			while ( soundLevel < soundThreshold );
			timerStart = System.currentTimeMillis();
			do
			{
				// Stopwatch running
				soundLevel = soundSensor.readValue();
				LCD.drawInt(soundLevel,4,10,0); 
			}
			while ( soundLevel > soundThreshold );
			timerStop = System.currentTimeMillis();
			diff = timerStop - timerStart;
			//LCD.drawInt((int)diff,4,10,1);
		}
		while( !(25 < diff && diff < 110));
	}

	public static void main(String[] args) throws Exception {
		soundSensor.setTypeAndMode(SoundSensor.TYPE_SOUND_DB, SoundSensor.MODE_PCTFULLSCALE);
		while (! Button.ESCAPE.isDown())
		{
			waitForClap();		
			LCD.drawString("Clap Count: " + clapCount,0,1);
			clapCount++;
		}
	}

}
