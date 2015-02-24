import lejos.nxt.*;
/**
 * A locomotion module with methods to drive
 * a car with two independent motors. The left motor 
 * should be connected to port C and the right motor
 * to port B.
 *  
 * @author  Ole Caprani
 * @version 17.4.08
 */
public class Car 
{
    // Commands for the motors
    public final static int forward  = 1,
                            backward = 2,
                            stop     = 3,
                            floats   = 4;
	                         
    private static MotorPort leftMotor = MotorPort.C;
    private static MotorPort rightMotor= MotorPort.B;
	
    private Car()
    {	   
    } 
   
    public static void stop() 
    {
	    leftMotor.controlMotor(0,stop);
	    rightMotor.controlMotor(0,stop);
    }
   
    public static void forward(int leftPower, int rightPower)
    {
	    leftMotor.controlMotor(leftPower,forward);
	    rightMotor.controlMotor(rightPower,forward);
    }
   
    public static void backward(int leftPower, int rightPower)
    {
	    leftMotor.controlMotor(leftPower,backward);
	    rightMotor.controlMotor(rightPower,backward);
    }
    
    public static void controlLeft(int power, int mode) {
    	leftMotor.controlMotor(power, mode);
    }
    
    public static void controlRight(int power, int mode) {
    	rightMotor.controlMotor(power, mode);
    }
}
