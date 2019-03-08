package trafficsimulator.simulator.interfaces;

import trafficsimulator.simulator.exceptions.TraciException;

/**
 * Interface for controlling of signal programs of simulation
 * @author z003ru0y
 *
 */
public interface SignalProgramControl
{
  /**
   * Sets the signal program of the intersection online
   * @param intersectionId intersection id 
   * @param signalProgramId signal program id
   * @throws TraciException when some of the traci calls fails
   */
  void setSignalProgramToIntersection(int intersectionId, String signalProgramId) throws TraciException;
}
