import lejos.nxt.*;
import lejos.nxt.addon.GyroSensor;
import lejos.util.Delay;

public class SejwayGyro 
{
	private static final float KGYROANGLE = 7.5f;
	private static final float KGYROSPEED = 15.15f;
	private static final float KPOS = 0.07f;
	private static final float KSPEED = 0.1f;	
	
    GyroSensor gyro;	
	private float power;
	private float gyroSpeed;
	private float gyroAngle;
	private int motorPos;
	private int motorSpeed;
	private float tInterval = 0.01f;
	private float globalAngle;
	
    public SejwayGyro() 
    {
        gyro = new GyroSensor(SensorPort.S2);              
    }
	
    public void getBalancePos() 
    {
    	LCD.drawString("Put down NXT", 0, 0);
    	Button.waitForAnyPress();
        
        LCD.drawString("Calibrating...", 0, 0);
        gyro.recalibrateOffset();        
        
        MotorPort.B.resetTachoCount();
        MotorPort.C.resetTachoCount();
        
        LCD.drawString("Balance NXT", 0, 0);
    	Button.waitForAnyPress();                
    }       
	
    public void pidControl() 
    {
        while (!Button.ESCAPE.isDown()) 
        {                   
            updateGyroData(gyro);
            updateMotorData();
            
            power = KGYROSPEED * gyroSpeed;
            		//KGYROANGLE * gyroAngle; 
            		//KPOS * motorPos +
            		//KSPEED * motorSpeed;
            
            MotorPort.B.controlMotor((int)power, BasicMotorPort.FORWARD);
            MotorPort.C.controlMotor((int)power, BasicMotorPort.FORWARD);
            
            LCD.clear();
            LCD.drawString("gyroSpeed:", 0, 1);            
            LCD.drawString("gyroAngle:", 0, 2);
            LCD.drawString("motorPos:", 0, 3);
            LCD.drawString("motorSpeed:", 0, 4);
            LCD.drawString("power:", 0, 5);
            LCD.drawInt((int)gyroSpeed, 10, 1);
            LCD.drawInt((int)gyroAngle, 4, 10, 2);
    		LCD.drawInt(motorPos, 4, 10, 3);
    		LCD.drawInt(motorSpeed, 4, 10, 4);
    		LCD.drawInt((int)power, 4, 10, 5);
            LCD.refresh();
               
            Delay.msDelay(10);                                    
        }
    }
	
    private void updateMotorData() {		
		motorPos = Motor.B.getTachoCount();
		motorSpeed = Motor.B.getRotationSpeed();
	}

	private void updateGyroData(GyroSensor gyro) {
    	gyroSpeed = gyro.getAngularVelocity();
		globalAngle += gyroSpeed * tInterval;
		gyroAngle = globalAngle;
	}
	
    public static void main(String[] args) 
    {
        SejwayGyro sej = new SejwayGyro();
        sej.getBalancePos();
        sej.pidControl();        
    }
}