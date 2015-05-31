
import java.util.Random;

import lejos.robotics.*;
import lejos.robotics.mapping.RangeMap;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.Pose;
import lejos.geom.*;

/**
 * Represents a particle for the particle filtering algorithm. The state of the
 * particle is the pose, which represents a possible pose of the robot.
 * 
 * A version of the leJOS class MCLParticle modified for position tracking. The weights
 * and weight updates have been left out.
 * 
 * @author  Ole Caprani
 * @version 18.05.15
 */
public class Particle 
{
  private static Random rand = new Random();
 
  private Pose pose;

  /**
   * Create a particle with a specific pose
   * 
   * @param pose the pose
   */
  public Particle(Pose pose) 
  {
    this.pose = pose;
  }

  /**
   * Return the pose of this particle
   * 
   * @return the pose
   */
  
  public Pose getPose() 
  {
    return pose;
  }
  
  /**
   * Apply the robot's move to the particle with a bit of random noise.
   * Only works for rotate or travel movements.
   * 
   * This is the original version from MCLParticle
   * 
   * @param move the robot's move
   */
  public void applyMove(Move move, float distanceNoiseFactor, float angleNoiseFactor) 
  {
    float ym = (move.getDistanceTraveled() * ((float) Math.sin(Math.toRadians(pose.getHeading()))));
    float xm = (move.getDistanceTraveled() * ((float) Math.cos(Math.toRadians(pose.getHeading()))));

    pose.setLocation(new Point(
    		         (float) (pose.getX() + xm + (distanceNoiseFactor * move.getDistanceTraveled() * rand.nextGaussian())),
                     (float) (pose.getY() + ym + (distanceNoiseFactor * move.getDistanceTraveled() * rand.nextGaussian()))));
    pose.setHeading(
       (float) (pose.getHeading() + move.getAngleTurned() + (angleNoiseFactor * move.getAngleTurned() * rand.nextGaussian())));
    pose.setHeading((float) ((int) (pose.getHeading() + 0.5f) % 360));
  }  
}
