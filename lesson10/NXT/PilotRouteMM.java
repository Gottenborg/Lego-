import lejos.nxt.*;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Move;

public class PilotRouteMM
{
    private SlaveIOStreams PC;
    
    double mult = 1.007;
    double leftWheelDiameter = 55 * mult, rightWheelDiameter = 54.84 * mult, trackWidth = 165;
    double travelSpeed = 50, rotateSpeed = 45;
    NXTRegulatedMotor left = Motor.A;
    NXTRegulatedMotor right = Motor.C;
	   
    DifferentialPilot pilot = new DifferentialPilot(leftWheelDiameter, rightWheelDiameter, trackWidth, left, right, false);

	public PilotRouteMM(boolean usb) 
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
	
	private void travel(double distance)
	{	
		pilot.travel(distance);
		sendMove(pilot.getMovement());
	}
	
	private void rotate(double angle)
	{	
		pilot.rotate(angle);
		sendMove(pilot.getMovement());
	}
	
	public void go()
	{				
		Sound.beep();
		while ( ! Button.ENTER.isDown()) Thread.yield();
		Sound.twoBeeps();
		
		travel(500);
		rotate(90);
		travel(200);
		rotate(-90);
		travel(500);	
		
		/*
		travel(500);
		rotate(90);	
		travel(200);
		rotate(-90);
		travel(500);
		rotate(90);
		travel(200);
		rotate(90);
		travel(700);
		rotate(90);
		travel(200);
		 */
		
		while ( ! Button.ENTER.isDown()) Thread.yield();
		
        LCD.clear();	
        LCD.drawString("Closing",0,0);
    	if ( PC.close() ) LCD.drawString("Closed",0,0); 
        try {Thread.sleep(2000);} catch (Exception e){}
	}
	
	
	public static void main(String[] args) 
	{
		PilotRouteMM route = new PilotRouteMM(false);
		
		LCD.clear();
		LCD.drawString("PilotRouteMM", 0, 0);
		route.go();		
	}
}
