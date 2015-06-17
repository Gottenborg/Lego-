/**
 * The RobotMonitor simulates a sequence of moves and sensor readings
 * and use these to update a set of particles in a 2D black/white tile world.
 * 
 * @author Ole Caprani 
 * @version 23-5-2015
 */

import java.awt.Color;

import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.Pose;
import lejos.util.Delay;

public class RobotMonitor 
{
    private InputOutputStreams NXT;
    private boolean USB = false;
//	private Map m = new Map(12, 1, 42*4); // 1D
	private Map m = new Map(11, 7, (int)(11.5*8)); // 2D                      
    private ParticleSet particles = new ParticleSet(5000, m);
    private Route route = new Route(11.5f*4,11.5f*4,0); // 2D
//  private Route route = new Route(21*4,21*4,0); // 1D
    private RobotGUI view = new RobotGUI(particles, m);
    private Move move;
    private int lightVal = 0;
    private int pause = 1000; // ms between views

    public RobotMonitor()
    {
        int[][] colors = {
        		{0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0},
        		{1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0},
        		{0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0},
        		{0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 1},
        		{0, 1, 1, 1, 1, 0, 1, 0, 1, 0, 0},
        		{0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0},
        		{0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0},        		        
        };
        System.out.println("i " + colors.length);
        System.out.println("j " + colors[0].length);
        for (int i = 0; i < 7; i++ ) {
        	for (int j = 0; j < 11; j++) {
        		if (colors[i][j] == 1) m.setColor(j, 6-i, Color.BLACK);
        	}
        }    	        
   
    	view.update(route.getRoute());
//    	Delay.msDelay(pause);
    	
    	String m;
    	NXT = new InputOutputStreams(USB);
        do {
        	m = NXT.open();
        	System.out.println(m);
        } while ( m != "Connected");
    }
    
    public void printParticles()
    {
    	for ( int i=0; i < particles.numParticles(); i++)
    	{
    		Pose p=particles.getParticle(i).getPose();
    		float w = particles.getParticle(i).getWeight();
    		System.out.println("P " + i + " "+p.getX()+" "+p.getY()+" "+p.getHeading()+" "+w);
    	}
    	System.out.println();
    }

    public void motionUpdate(Move move)
    {
    	particles.applyMove(move);
    	route.update(move);
    	view.update(route.getRoute()); 
    	Delay.msDelay(pause);
    }
    
    public void sensorUpdate(int lightValue)
    {
    	particles.calculateWeights(lightValue, m);
    	view.update(route.getRoute()); 
    	Delay.msDelay(pause);
    	particles.resample();
    	view.update(route.getRoute());
    	Delay.msDelay(pause);
    }
    
    public void goSimulation()
    {
    	
    	for ( int i=0; i < 16; i++)
    	{
    		move = new Move(Move.MoveType.TRAVEL, 9, 0, false);
    		motionUpdate(move);
    		sensorUpdate(400);
    	}
    	
    	for ( int i=0; i < 5; i++)
    	{
    		move = new Move(Move.MoveType.TRAVEL, 10, 0, false);
    		motionUpdate(move);
    		sensorUpdate(600);
    	}

		move = new Move(Move.MoveType.TRAVEL, 100, 0, false);
    	motionUpdate(move);
    	sensorUpdate(600);
    	
		move = new Move(Move.MoveType.TRAVEL, 10, 0, false);
    	motionUpdate(move);
    	sensorUpdate(400);
    	
		move = new Move(Move.MoveType.TRAVEL, 10, 0, false);
    	motionUpdate(move);
    	sensorUpdate(400);
    	    	
    	Pose p = particles.getPose();
		System.out.println("Position "+p.getX()+" "+p.getY()+" "+p.getHeading());
		System.out.println("Accuracy "+ particles.getSigmaX()+ " "+ particles.getSigmaY()+" "+particles.getSigmaHeading());
    	
    }
    
    private Move getMove()
    {
    	Move m;
    	int type = (int)NXT.input();
//    	float distance = NXT.input() * 0.4f; // 1D
    	float distance = NXT.input() * 0.8f; // 2D
    	float angle = NXT.input();
    	m = new Move( (type == 0)? Move.MoveType.TRAVEL : Move.MoveType.ROTATE , 
    			distance, angle, false);
    	System.out.println("Move " + distance + " " + angle);
    	return m;
    }
    
    public void go()
    {
        while ( true )
        {
        	move = getMove();
        	lightVal = (int) NXT.input();
        	
        	motionUpdate(move);
        	sensorUpdate(lightVal); 
        	Pose p = particles.getPose();
        	
        	// Check if robot have located itself.        	        	
//        	float haveLocation = (particles.getSigmaX() < 50 &&
//        			particles.getSigmaY() < 50) ? 1 : 0; // 2D
        	float haveLocation = (particles.getErrorRect().getWidth() < 42) ? 1 : 0; // 1D
        	NXT.output(haveLocation);        	
        	
    		System.out.println("Position "+p.getX()+" "+p.getY()+" "+p.getHeading());
    		System.out.println("Accuracy "+ particles.getSigmaX()+ " "+ particles.getSigmaY()+" "+particles.getSigmaHeading());
        }
    }
    
    public static void main (String [] args)
    {
    	RobotMonitor p = new RobotMonitor();
    	
    	p.go();

     }
}