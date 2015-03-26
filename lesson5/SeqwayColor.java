import lejos.nxt.*;

public class SeqwayColor {
    // Global vars:
    int offset;
    int prev_error;
    float int_error;
	
    ColorSensor cs; 
    static DataLogger dl;

    // PID constants
    final int KP = 45;
    final int KI = 0;
    final int KD = 1;
    final int SCALE = 1;
	
	public SeqwayColor() 
	{
		cs = new ColorSensor(SensorPort.S3);
	}

	public void getBalancePos() 
	{
		// Wait for user to balance and press orange button
		while (!Button.ENTER.isDown())
		{
			// NXTway must be balanced.
			offset = cs.getNormalizedLightValue();
			LCD.clear();
			LCD.drawInt(offset, 2, 4);
			LCD.refresh();
		}
	}
	
	public void pidControl() 
    {
        while (!Button.ESCAPE.isDown()) 
        {
            int normVal = cs.getNormalizedLightValue();

            // Proportional Error:
            int error = normVal - offset;
			
            // Integral Error:
            int_error = int_error + error;
			
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

//            dl.writeSample(error);
//            dl.writeSample((int)int_error);
//            dl.writeSample(deriv_error);
//            dl.writeSample(pid_val);

            if (pid_val > 0) {
                MotorPort.B.controlMotor(power, BasicMotorPort.FORWARD);
                MotorPort.C.controlMotor(power, BasicMotorPort.FORWARD);
            } else {
                MotorPort.B.controlMotor(power, BasicMotorPort.BACKWARD);
                MotorPort.C.controlMotor(power, BasicMotorPort.BACKWARD);
            }
        }
    }
	
    public void shutDown()
    {
        // Shut down light sensor, motors
        Motor.B.flt();
        Motor.C.flt();
        cs.setFloodlight(false);
//        dl.close();
    }

	public static void main(String args[]) {
//		dl = new DataLogger("data.txt");
//		dl.start();
		SeqwayColor sw = new SeqwayColor();
		sw.getBalancePos();
        sw.pidControl();
        sw.shutDown();
	}
}
