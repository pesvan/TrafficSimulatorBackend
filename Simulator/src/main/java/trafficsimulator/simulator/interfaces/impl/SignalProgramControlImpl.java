package trafficsimulator.simulator.interfaces.impl;

import trafficsimulator.simulator.exceptions.TraciException;
import trafficsimulator.simulator.interfaces.SignalProgramControl;
import trafficsimulator.simulator.traci.TraciService;

/**
 * @author z003ru0y
 *
 */
public class SignalProgramControlImpl implements SignalProgramControl
{  
  private final TraciService traciService;
  
  /**
   * @param traciService traci service
   */
  public SignalProgramControlImpl(TraciService traciService)
  {
    this.traciService = traciService;
  }

  @Override
  public void setSignalProgramToIntersection(int intersectionId, String signalProgramId) throws TraciException
  {
    traciService.setSignalProgram(String.valueOf(intersectionId), signalProgramId);
  }
}
