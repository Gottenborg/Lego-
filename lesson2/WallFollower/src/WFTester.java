import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.util.Delay;


public class WFTester {

	public static void main (String[] aArg)	{
		int distance, power;
		WallFollower follower = new WallFollower();

		LCD.drawString("WallFollower", 0, 0);
		LCD.drawString("Distance: ", 0, 3);
		LCD.drawString("LeftPower:    ", 0, 4);
		LCD.drawString("RightPower:    ", 0, 5);
		
		while (! Button.ENTER.isDown()) {
			Delay.msDelay(100);
		}			

		follower.go();

		while (! Button.ESCAPE.isDown())
		{		   
			distance = follower.getDistance();
			//power = follower.getPower();

			LCD.drawInt(distance,4,10,3);
			LCD.drawInt(follower.getLeftPower(), 4,10,4);
			LCD.drawInt(follower.getRightPower(), 4,10,5);

			Delay.msDelay(30);
		}

		follower.stop();
		LCD.clear();
		LCD.drawString("Program stopped", 0, 0);
		Delay.msDelay(2000);
	}
}
