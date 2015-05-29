
import lejos.geom.*;
import lejos.robotics.*;
import lejos.robotics.mapping.RangeMap;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.Pose;
import java.util.Random;

/**
 * Represents a particle set for the particle filtering algorithm.
 *
 * A version of the leJOS class MCLParticleSet modified for position tracking. 
 * The initial set consist only of a known start position Pose(0,0,0). 
 * 
 * @author  Ole Caprani
 * @version 18.05.15
 *
 */
public class ParticleSet
{

  // Instance variables
  private float distanceNoiseFactor = 0.2f; //0.2f;
  private float angleNoiseFactor = 4; //4f;
  private int numParticles;
  private Particle[] particles;
  
  /**
   * Create a set of particles. All particles are identical and equal to
   * Pose(0,0,0), i.e. the robot starts in (0,0) heading in the positive
   * direction of the x-axis.
   *
   */
  public ParticleSet(int numParticles)
  {
    this.numParticles = numParticles;
    particles = new Particle[numParticles];
    
    for (int i = 0; i < numParticles; i++) 
    {
      particles[i] = new Particle(new Pose(0, 0, 0));
    }
  }

  /**
   * Return the number of particles in the set
   *
   * @return the number of particles
   */
  public int numParticles() 
  {
    return numParticles;
  }

  /**
   * Get a specific particle
   *
   * @param i the index of the particle
   * @return the particle
   */
  public Particle getParticle(int i) 
  {
    return particles[i];
  }

  /**
   * Apply a move to each particle
   *
   * @param move the move to apply
   */
  public void applyMove(Move move) 
  {
    for (int i = 0; i < numParticles; i++) 
    {
      particles[i].applyMove(move, distanceNoiseFactor, angleNoiseFactor);
    }
  }
  
  /**
   * Set the distance noise factor
   * @param factor the distance noise factor
   */
  public void setDistanceNoiseFactor(float factor) 
  {
    distanceNoiseFactor = factor;
  }

  /**
   * Set the distance angle factor
   * @param factor the distance angle factor
   */
  public void setAngleNoiseFactor(float factor) 
  {
    angleNoiseFactor = factor;
  }

}
