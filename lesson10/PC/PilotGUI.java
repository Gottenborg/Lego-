
/**
 * The PilotGUI Class
 * 
 * This class can be used to visualize the actions of a particle filter 
 * Localization algorithm while a vehicle drives a route.
 * 
 * @author Ole Caprani
 * @version 14.05.2015
 */

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.JFrame;
import lejos.geom.Point;
import lejos.robotics.navigation.Pose;

public class PilotGUI extends JFrame {

    // program will automatically adjust if these three constants are changed
    static final int VIS_HEIGHT=1080;  // visualization height  
    static final int VIS_WIDTH=1920;   // visualization width
    static final int Y_OFFSET=200;     // vertical offset to visualization
    static final int X_OFFSET=200;     // horizontal offset to visualization
    static final int POINT_WIDTH=1;  // width of points shown in the visualization
                                      // VIS_HEIGHT + 2 * Y_OFFSET is the window height
                                      // VIS_WIDTH + 2 * X_OFFSET is the window width
    
    static final int GUI_SCALE = 1;
    
    
    ParticleSet particles;            // the set of sample possible locations
    ArrayList<Point> route;           // the route as seen from the vehicle
    
    ArrayList<ParticleSet> particleSets = new ArrayList<ParticleSet>();
	private int desX;
	private int desY;
    
    // an inner class to handle the visualization window being closed manually
    class MyWindowAdapter extends WindowAdapter {   
        public void windowClosing(WindowEvent w) {
            System.exit(0);
        }
    }
    
    // the constructor for the Visualize class    
    public PilotGUI(ParticleSet particles, int desX, int desY) 
    { 
    	 this.particles = particles;
    	 this.desX = desX;
    	 this.desY = desY;
         this.setSize(new Dimension(VIS_WIDTH+X_OFFSET*2, VIS_HEIGHT+Y_OFFSET*2));
         this.setTitle("Monte Carlo Localization");
         this.setVisible(true);
         this.setBackground(Color.WHITE);
         addWindowListener(new MyWindowAdapter());
    }
    
    // update the visualization display based on the estimated vehicle location and
    // and the locations of the samples
    public void update(ArrayList<Point> route, boolean isRotate) 
    {
    	this.route = route;
    	if (isRotate) {
    		// Remove last move since rotate will appear on top.
    		particleSets.remove(particleSets.size() - 1);
    	}
    	particleSets.add(new ParticleSet(particles));    	
        repaint();
    }
        
    // paint the visualization window - called by repaint in update method, and also by the
    // run-time system whenever it decides the window need to be updated (e.g., when uncovered)
    public void paint(Graphics g) 
    {                    

        Graphics2D g2 = ( Graphics2D ) g; 
        Pose p; Float w;
        
        super.paint(g);
        g2.setColor(Color.RED);
        g2.drawRect(winX(0), winY(VIS_HEIGHT), VIS_WIDTH, VIS_HEIGHT);
        
        g2.setColor(Color.BLUE);
        int x1 = winX(0), y1 = winY(0), x2, y2;
        int size = route.size();
        for (int i = 1; i < size ; i++) 
        { 
        	Point current = route.get(i);
            x2 = winX((int)current.getX() * GUI_SCALE);
        	y2 = winY((int)current.getY() * GUI_SCALE);
        	g2.drawLine(x1, y1, x2, y2);
        	x1 = x2; y1 = y2;
        }
        
        for (int j = 0; j < particleSets.size(); j++) {
        	for (int i = 0; i < particleSets.get(j).numParticles(); i++) 
            {
                p = particleSets.get(j).getParticle(i).getPose();
                g2.setColor(Color.BLACK);
                g2.fillRect(winX((int)p.getX() * GUI_SCALE - POINT_WIDTH / 2) , 
                		    winY((int)p.getY() * GUI_SCALE + POINT_WIDTH / 2), 
                		    POINT_WIDTH, POINT_WIDTH);          
            }
        }
        
        // Draw destination.
        g2.setColor(Color.RED);
        g2.fillOval(winX(desX) - 5, winY(desY) + 5, 10, 10);
    }
    
    private int winX(int x)
    {
    	return x + X_OFFSET;
    }
    
    private int winY(int y)
    {
    	return VIS_HEIGHT - y + Y_OFFSET;
    }

}
