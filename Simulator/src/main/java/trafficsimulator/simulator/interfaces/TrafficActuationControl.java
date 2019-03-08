package trafficsimulator.simulator.interfaces;

import java.util.List;

import trafficsimulator.shared.dto.Intersection;
import trafficsimulator.simulator.exceptions.TraciException;

/**
 * Interface responsible for controlling traffic actuation in the simulation
 * @author z003ru0y
 *
 */
public interface TrafficActuationControl
{
  /**
   * Performs traffic actuation algorithm for the current step
   * @param intersectionList list of intersection to perform TA on
   * @throws TraciException when some of the traci calls fails
   */
  void doTrafficActuation(List<Intersection> intersectionList) throws TraciException;
}
