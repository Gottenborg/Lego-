import lejos.nxt.*;
/**
 * A simple line follower for the LEGO 9797 car with
 * a light sensor. Before the car is started on a line
 * a BlackWhiteSensor is calibrated to adapt to different
 * light conditions and colors.
 * 
 * The light sensor should be connected to port 3. The
 * left motor should be connected to port C and the right 
 * motor to port B.
 * 
 * @author  Ole Caprani
 * @version 20.02.13
 */
public class PIDLineFollower
{
  

public static void main (String[] aArg)
  throws Exception
  {     
     BlackWhiteSensor sensor = new BlackWhiteSensor (SensorPort.S1);
	 
     sensor.calibrate();
     int offset = sensor.median();
     //int offset = 46;
     
     final double dT = 0.015;
     final double Pc = 1;
     
     double Kp = 150 * 0.6; // Kc = 150, 0.6 * KC
     double Ki = 2 * Kp * dT / Pc; // 2 * Kp * dT / Pc
     double Kd = Kp * Pc / (8 * dT); // Kp * Pc / (8 * dT)
     final int Tp = 65;
     int integral = 0;
     int lastError = 0;
     int derivative = 0;
     
     while (!Button.ENTER.isUp());
     while (!Button.ENTER.isDown());
	 
     LCD.clear();
     LCD.drawString("Light: ", 0, 1);
     LCD.drawString("Error: ", 0, 2);
     LCD.drawString("Integral: ", 0, 3);
     LCD.drawString("Derivative: ", 0, 4);
     LCD.drawString("Turn: ", 0, 5);
	 
     while (! Button.ESCAPE.isDown())
     {
	     LCD.drawInt(sensor.light(),4,10,1);
	     LCD.refresh();
	    
	     int lightValue = sensor.light();
	     int error = lightValue - offset;
	     integral += error;
	     derivative = error - lastError;
	     
	     double turn = Kp * error + Ki * integral + Kd * derivative;
	     turn /= 100;
	     int powerLeft = Tp - (int)turn;
	     int powerRight = Tp + (int)turn;
	     
	     Car.forward(powerLeft, powerRight);
	     
	     lastError = error;
	     
	     LCD.drawInt(error, 4, 10, 2);
	     LCD.drawInt(integral, 4, 10, 3);
	     LCD.drawInt(derivative, 4, 10, 4);
	     LCD.drawInt((int)turn, 4, 10, 5);
	     
	     Thread.sleep(10);
     }
     
     Car.stop();
     LCD.clear();
     LCD.drawString("Program stopped", 0, 0);
     LCD.refresh();
   }
}
