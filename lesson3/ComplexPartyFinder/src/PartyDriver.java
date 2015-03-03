import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.util.Delay;


public class PartyDriver {

	public static void main (String[] aArg)	{
		PartyFinder follower = new PartyFinder();

		LCD.drawString("PartyFinder", 0, 0);
		LCD.drawString("Diff Avg:    ", 0, 2);
		LCD.drawString("Diff:    ", 0, 3);
		LCD.drawString("LeftSound:    ", 0, 4);
		LCD.drawString("RightSound:    ", 0, 5);
		
		while (! Button.ENTER.isDown()) {
			Delay.msDelay(100);
		}			

		follower.go();

		while (! Button.ESCAPE.isDown())
		{		   
			LCD.drawInt(follower.getLeftSound(), 4,10,4);
			LCD.drawInt(follower.getRightSound(), 4,10,5);

			Delay.msDelay(30);
		}

		follower.stop();
		LCD.clear();
		LCD.drawString("Program stopped", 0, 0);
		Delay.msDelay(2000);
	}
}
