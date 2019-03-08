package trafficsimulator.server.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.Getter;
import trafficsimulator.shared.dto.Situation;
import trafficsimulator.simulator.interfaces.SimulationControl;
import trafficsimulator.simulator.control.SimulationControlFactory;
import trafficsimulator.simulator.exceptions.NoIntersectionInSituationException;
import trafficsimulator.simulator.exceptions.NoValidRoutesInSituationException;
import trafficsimulator.simulator.exceptions.TraciException;

/**
 * @author z003ru0y
 * Contains current relevant data about ongoing simulation
 */
public class SessionMemory
{
  private static Logger logger = LoggerFactory.getLogger(SessionMemory.class);

  @Getter
  private Situation situation;

  private SimulationControl simulationControl;
  
  @Getter
  private final int visualizationMultiplier;
 
  @Autowired
  private SimulationControlFactory simulationControlFactory;

  /**
   * creates new instance and sets up new situation
   * @param visualizationMultiplier how to multiply vehicle dimensions for web
   */
  public SessionMemory(int visualizationMultiplier)
  {    
    this.visualizationMultiplier = visualizationMultiplier;
    setUpNewSessionData();
    logger.debug("Session Memory started");
  }

  /**
   * creates new situation layout
   */
  public void setUpNewSessionData()
  {
    situation = new Situation();
    logger.debug("New situation set");
  }

  /**
   * Retrieves current simulation control
   * @param createNewIfNull if true, than the instance is created if its missing
   * @return current simulation control instance
   * @throws NoIntersectionInSituationException when there are no intersections present in infrastructure
   * @throws NoValidRoutesInSituationException when there are no valid routes present in infrastructure
   * @throws TraciException when some of the traci calls fails
   */
  public SimulationControl getSimulationControl(boolean createNewIfNull)
      throws NoIntersectionInSituationException, NoValidRoutesInSituationException, TraciException
  {
    if (simulationControl == null && createNewIfNull)
    {
      logger.debug("Creating new simulation control");
      simulationControl = simulationControlFactory.createSimulationControl(situation);      
      return simulationControl;
    }
    logger.debug("Using existing simulation control");
    return simulationControl;
  }

  /**
   * removes current instance of simulation control
   */
  public void deleteCurrentRunner()
  {
    simulationControl = null;
    logger.debug("Simulation control removed");
  }
  
  /**
   * @return true if simulation control has current instance set up
   */
  public boolean hasSimulationControl()
  {
    return simulationControl != null;
  }

}
