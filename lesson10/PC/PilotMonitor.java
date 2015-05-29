/**
 * The PilotMonitor receives move parameters from a
 * vehicle drining in a route of DifferentialPilot
 * travel or rotate steps. The move parameters are used to update
 * a visualization of the route and possible locations of
 * the vehicle after each step.
 * 
 * @author Ole Caprani 
 * @version 18-5-2015
 */


import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.Pose;

public class PilotMonitor 
{
	private InputOutputStreams NXT;
	private boolean USB = false;
	private Route route = new Route();
    private ParticleSet particles  = new ParticleSet(10);
    private PilotGUI view = new PilotGUI(particles);
    private Move move;
    
    public PilotMonitor()
    {
    	String m;
        view.update(route.getRoute());
        NXT = new InputOutputStreams(USB);
        do {
        	m = NXT.open();
        	System.out.println(m);
        } while ( m != "Connected");
    }
    
    private Move getMove()
    {
    	Move m;
    	int type = (int)NXT.input();
    	float distance = NXT.input();
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
        	particles.applyMove(move);
        	route.update(move);
        	view.update(route.getRoute());
        	Pose p = route.getCurrentPose();
            System.out.println("Pose " + p.getX() + " " + p.getY() + " " + p.getHeading());
        }
    }
   
    public static void main (String [] args)
    {
         PilotMonitor monitor = new PilotMonitor();
         
         monitor.go();
    }

}