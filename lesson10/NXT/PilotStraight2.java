import lejos.nxt.*;
import lejos.nxt.comm.RConsole;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.Pose;
import lejos.util.Delay;
/**
 * A program that uses the DifferentialPilot to make a differential
 * driven car drive in a square. During the drive the OdometryPoseProvider
 * is used to display the Pose of the car on the LCD after each
 * movement of the car.
 * 
 * Use the PC tool nxjconsoleviewer to show the LCD output on a PC
 * for easier inspection.
 * 
 * @author  Ole Caprani
 * @version 13.05.15
 */
public class PilotStraight2 
{
   private static void show(Pose p)
   {
      LCD.clear();
       LCD.drawString("Pose X " + p.getX(), 0, 2);
       LCD.drawString("Pose Y " + p.getY(), 0, 3);
       LCD.drawString("Pose V " + p.getHeading(), 0, 4);
   }

   public static void main(String [] args)  
   throws Exception 
   {
	   double mult = 1.007;
       double leftWheelDiameter = 5.5 * mult, rightWheelDiameter = 5.484 * mult, trackWidth = 16.5;
       double travelSpeed = 5, rotateSpeed = 45;
       NXTRegulatedMotor left = Motor.A;
       NXTRegulatedMotor right = Motor.C;
	   
       DifferentialPilot pilot = new DifferentialPilot(leftWheelDiameter, rightWheelDiameter, trackWidth, left, right, false);
       OdometryPoseProvider poseProvider = new OdometryPoseProvider(pilot);
       Pose initialPose = new Pose(0,0,0);
       pilot.setTravelSpeed(travelSpeed);
       pilot.setRotateSpeed(rotateSpeed);
       
       LCD.drawString("PilotStraightLoop", 0, 0);
       Button.waitForAnyPress();
       
       while (!Button.ESCAPE.isDown()) {
    	   poseProvider.setPose(initialPose);           
           pilot.travel(50);           
           show(poseProvider.getPose());
           Button.waitForAnyPress();
       }       
       
       pilot.stop();
       LCD.drawString("Program stopped", 0, 0);
       Button.waitForAnyPress();
       Thread.sleep(2000);
   }
}
