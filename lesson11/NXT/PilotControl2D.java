import lejos.nxt.*;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Move;
import lejos.util.Delay;

public class PilotControl2D
{
    private SlaveIOStreams PC;
    private LightSensor light = new LightSensor(SensorPort.S1);
    
    double mult = 1.007;
    double leftWheelDiameter = 55 * mult, rightWheelDiameter = 54.84 * mult, trackWidth = 165;
    double travelSpeed = 50, rotateSpeed = 45;
    NXTRegulatedMotor left = Motor.A;
    NXTRegulatedMotor right = Motor.C;
	   
    DifferentialPilot pilot = new DifferentialPilot(leftWheelDiameter, rightWheelDiameter, trackWidth, left, right, false);
	private boolean haveLocation = false;

	public PilotControl2D(boolean usb) 
	{
    	PC = new SlaveIOStreams(usb);
    	PC.open();
 	   
 	    pilot.setTravelSpeed(travelSpeed);
 	    pilot.setRotateSpeed(rotateSpeed);
	}
	
	private void sendMove(Move move)
	{	
		PC.output((move.getMoveType() == Move.MoveType.TRAVEL? 0:1 ));
		PC.output(move.getDistanceTraveled());
		PC.output(move.getAngleTurned());
	}
	
	private void sendLight() {
		PC.output(light.getLightValue());
	}
	
	private void travel(double distance)
	{	
		pilot.travel(distance);
		sendMove(pilot.getMovement());
		PC.output(light.readNormalizedValue());
	}
	
	private void rotate(double angle)
	{	
		pilot.rotate(angle);
		sendMove(pilot.getMovement());
		PC.output(light.readNormalizedValue());
	}
	
	public void go()
	{				
		Sound.beep();
		while ( ! Button.ENTER.isDown()) Thread.yield();
		Sound.twoBeeps();
		
		travel(0);
		PC.input();
		for (int i = 0; i < 6; i++)	{
			travel(115);
			Delay.msDelay(500);
			if (PC.input() == 1) {
				// I have my location!
				Sound.buzz();
				haveLocation = true;
				break;
			}
    	}
		if (!haveLocation) {
			rotate(90);
			for(int j = 0; j < 4; j++) {
				travel(115);
				Delay.msDelay(500);
				if (PC.input() == 1) {
					// I have my location!
					Sound.buzz();
					haveLocation = true;
					break;
				}
			}
		}
		if (!haveLocation) {
			rotate(90);
			for(int j = 0; j < 4; j++) {
				travel(115);
				Delay.msDelay(500);
				if (PC.input() == 1) {
					// I have my location!
					Sound.buzz();
					haveLocation = true;
					break;
				}
			}
		}
				
		while ( ! Button.ENTER.isDown()) Thread.yield();
		
        LCD.clear();	
        LCD.drawString("Closing",0,0);
    	if ( PC.close() ) LCD.drawString("Closed",0,0); 
        try {Thread.sleep(2000);} catch (Exception e){}
	}
	
	
	public static void main(String[] args) 
	{
		PilotControl2D route = new PilotControl2D(false);
		
		LCD.clear();
		LCD.drawString("PilotControl", 0, 0);
		route.go();		
	}
}
