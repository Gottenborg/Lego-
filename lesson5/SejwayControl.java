import java.io.DataInputStream;
import java.io.DataOutputStream;

import lejos.nxt.*;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

/**
 * A controller for a self-balancing Lego robot with a light sensor
 * on port 2. The two motors should be connected to port B and C.
 *
 * Building instructions in Brian Bagnall: Maximum Lego NXTBuilding 
 * Robots with Java Brains</a>, Chapter 11, 243 - 284
 * 
 * @author Brian Bagnall
 * @version 26-2-13 by Ole Caprani for leJOS version 0.9.1
 */


public class SejwayControl 
{
	private String connected = "Connected";
    private String waiting = "Waiting...";
    private String closing = "Closing...";
	
	private BTConnection btc;
    private DataInputStream dis;
    private DataOutputStream dos;

    // PID constants
    private int KP = 28;
    private int KI = 4;
    private int KD = 33;
    private int SCALE = 18;

    // Global vars:
    int offset;
    int prev_error;
    float int_error;
	
    LightSensor ls;
	
    public SejwayControl() 
    {
        ls = new LightSensor(SensorPort.S2, true);
        
        LCD.drawString(waiting,0,0);

        btc = Bluetooth.waitForConnection();
        
        LCD.clear();
        LCD.drawString(connected,0,0);	

        dis = btc.openDataInputStream();
        dos = btc.openDataOutputStream();
    }
	
    public void getBalancePos() 
    {
        // Wait for user to balance and press orange button
        while (!Button.ENTER.isDown())
        {
        // NXTway must be balanced.
        offset = ls.readNormalizedValue();
        LCD.clear();
        LCD.drawString("Offset:", 0, 1);
        LCD.drawInt(offset, 10, 1);
        LCD.drawString("KP:", 0, 2);
        LCD.drawString("KI:", 0, 3);
        LCD.drawString("KD:", 0, 4);
        LCD.drawString("SCALE:", 0, 5);
        LCD.drawInt(KP, 4, 10, 2);
		LCD.drawInt(KI, 4, 10, 3);
		LCD.drawInt(KD, 4, 10, 4);
		LCD.drawInt(SCALE, 4, 10, 5);
        LCD.refresh();
        }
        
        try {
        	dos.writeInt(offset);
        	dos.flush();
        } catch (Exception e) {
        	
        }
    }
	
    public void pidControl() 
    {
        while (!Button.ESCAPE.isDown()) 
        {
        	try {
        		if (dis.available() >= 16) {
        			KP = dis.readInt();
            		KI = dis.readInt();
            		KD = dis.readInt();
            		SCALE = dis.readInt();
            		offset = dis.readInt();
            		LCD.drawInt(offset, 10, 1);
            		LCD.drawInt(KP, 4, 10, 2);
            		LCD.drawInt(KI, 4, 10, 3);
            		LCD.drawInt(KD, 4, 10, 4);
            		LCD.drawInt(SCALE, 4, 10, 5);
            		LCD.refresh();
        		}           		
        	} catch (Exception e) {
        	}        	
        	
            int normVal = ls.readNormalizedValue();

            // Proportional Error:
            int error = normVal - offset;
            // Adjust far and near light readings:
            if (error < 0) error = (int)(error * 1.8F);
			
            // Integral Error:
            int_error = ((int_error + error) * 2)/3;
			
            // Derivative Error:
            int deriv_error = error - prev_error;
            prev_error = error;
			
            int pid_val = (int)(KP * error + KI * int_error + KD * deriv_error) / SCALE;
			
            if (pid_val > 100)
                pid_val = 100;	
            if (pid_val < -100)
                pid_val = -100;

            // Power derived from PID value:
            int power = Math.abs(pid_val);
            power = 55 + (power * 45) / 100; // NORMALIZE POWER


            if (pid_val > 0) {
                MotorPort.B.controlMotor(power, BasicMotorPort.FORWARD);
                MotorPort.C.controlMotor(power, BasicMotorPort.FORWARD);
            } else {
                MotorPort.B.controlMotor(power, BasicMotorPort.BACKWARD);
                MotorPort.C.controlMotor(power, BasicMotorPort.BACKWARD);
            }
            
            
            try {
            	dos.writeInt(normVal);
            	dos.writeInt(pid_val);
            	dos.flush();
            } catch (Exception e) {
            	
            }
            
        }
    }
	
    public void shutDown()
    {
        // Shut down light sensor, motors
        Motor.B.flt();
        Motor.C.flt();
        ls.setFloodlight(false);
        
        LCD.clear();
        LCD.drawString(closing,0,0);
        try {
    	    dis.close();
            dos.close();
            Thread.sleep(100); // wait for data to drain
            btc.close();    	
    	} catch (Exception e) {    		
    	}
    }
	
    public static void main(String[] args) 
    {
        SejwayControl sej = new SejwayControl();
        sej.getBalancePos();
        sej.pidControl();
        sej.shutDown();
    }
}