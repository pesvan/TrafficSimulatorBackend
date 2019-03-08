package trafficsimulator.simulator.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import trafficsimulator.shared.dto.Situation;
import trafficsimulator.simulator.exceptions.NoIntersectionInSituationException;
import trafficsimulator.simulator.exceptions.NoValidRoutesInSituationException;
import trafficsimulator.simulator.exceptions.TraciException;
import trafficsimulator.simulator.interfaces.SimulationControl;
import trafficsimulator.simulator.interfaces.impl.SimulationControlImpl;
import trafficsimulator.simulator.traci.TraciService;

/**
 * Creates new instances of Simulation Control
 * @author z003ru0y
 *
 */
public class SimulationControlFactory
{
  private static Logger logger = LoggerFactory.getLogger(SimulationControlFactory.class);
  
  private final double sumoSimulationStepLength;
  
  private final int sumoMaxVehicles;
  
  @Autowired
  private TraciService traciService;
  
  /**
   * @param stepLength simulation step length
   * @param maxVehicles maximum vehicles limitation
   */
  public SimulationControlFactory(double stepLength, int maxVehicles)
  {
    sumoSimulationStepLength = stepLength;
    sumoMaxVehicles = maxVehicles;
    logger.debug("Simulation Control Factory started");
  }
  
  /**
   * @param situation infrastructure for simulation
   * @return SimulationControl instance
   * @throws NoIntersectionInSituationException when there are no intersections present in infrastructure
   * @throws NoValidRoutesInSituationException when there are no valid routes present in infrastructure
   * @throws TraciException when some of the traci calls fails
   */
  public SimulationControl createSimulationControl(Situation situation)
      throws NoIntersectionInSituationException, NoValidRoutesInSituationException, TraciException
  {
    logger.debug("SimulationControl instance created");
    return new SimulationControlImpl(traciService, sumoSimulationStepLength, sumoMaxVehicles, situation);
  }
  
}
