package trafficsimulator.simulator.interfaces.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import trafficsimulator.shared.dto.Detector;
import trafficsimulator.shared.dto.Intersection;
import trafficsimulator.shared.dto.Lane;
import trafficsimulator.shared.dto.SignalProgram;
import trafficsimulator.shared.dto.SumoPhase;
import trafficsimulator.simulator.exceptions.TraciException;
import trafficsimulator.simulator.interfaces.TrafficActuationControl;
import trafficsimulator.simulator.traci.TraciService;
import trafficsimulator.simulator.traci.dto.PhaseInfo;

/**
 * @author z003ru0y
 *
 */
public class TrafficActuationControlImpl implements TrafficActuationControl
{
  
  private static Logger logger = LoggerFactory.getLogger(TrafficActuationControlImpl.class);
  
  private final TraciService traciService;
  
  /**
   * @param traciService traci service
   */
  public TrafficActuationControlImpl(TraciService traciService)
  {
    this.traciService = traciService;
  }
  
  @Override
  public void doTrafficActuation(List<Intersection> intersectionList) throws TraciException
  {    
    for (Intersection intersection : intersectionList)
    {
      for (Detector detector : intersection.getAllDetectors())
      {
        for (Lane lane : detector.getLanesUsed())
        {
          laneTrafficActuation(intersection, detector, lane);
        }
      }
    }
  }
  
  private void laneTrafficActuation(Intersection intersection, 
      Detector detector, Lane lane) 
      throws TraciException
  {
    boolean detectorOccupancy = traciService.detectorIsOccupied(detector.getId(), lane.getId());
    int phaseId = traciService.getTlsPhaseId(intersection.getId());
    String spProgram = traciService.getTlsSignalProgramId(intersection.getId());
    SignalProgram sp = getSPbyId(intersection.getSignalPrograms(), spProgram);
    List<String> sumoLanes = traciService.getControledLanes(intersection.getId());
    
    int laneIndex = getLaneIndex(sumoLanes, lane);
    
    PhaseInfo currentPhase = traciService.getCurrentIntersectionPhaseInfo(intersection.getId(),
      intersection.getSelectedSignalProgram().getProgramId());
    SumoPhase sumoPhase = getSumoPhaseById(sp.getSumoPhases(), phaseId);

    if (detectorOccupancy)
    {
       prolongPhase(currentPhase, sumoPhase, detector, intersection, laneIndex);      
    }
  }
  
  
  private int getLaneIndex(List<String> sumoLanes, Lane lane)
  {
    for (int i = 0; i < sumoLanes.size(); i++)
    {
      if (sumoLanes.get(i).equals(lane.getLaneNetId()))
      {
        return i;
      }
    }
    return -1;
  }
  
  
  private void prolongPhase(PhaseInfo currentPhase, SumoPhase sumoPhase, 
      Detector det, Intersection intersection, int laneIndex) throws TraciException
  {
    if (currentPhase.getSequence().charAt(laneIndex) == 'g' && sumoPhase.getPhase() != null)
    {

      if (currentPhase.getDuration() + det.getDemandResetThreshold() <= sumoPhase.getPhase().getMaxDuration())
      {
        logger.info("Traffic actuation used on intersection " + intersection.getId()
          + " at detector "
          + det.getId());

        traciService.setCurrentPhaseDuration(intersection.getId(),
          ((int)currentPhase.getDuration() + det.getRunningGap()) * 1000);

      }

    }
  }
  
  private SumoPhase getSumoPhaseById(List<SumoPhase> sumoPhaseList, int id)
  {
    for (SumoPhase sumoPhase : sumoPhaseList)
    {
      if (id == sumoPhase.getId())
      {
        return sumoPhase;
      }
    }

    // TODO throw not found ex
    return null;
  }

  private SignalProgram getSPbyId(List<SignalProgram> spList, String programId)
  {
    for (SignalProgram sp : spList)
    {
      if (programId.equals(sp.getProgramId()))
      {
        return sp;
      }
    }

    // TODO throw not found ex
    return null;
  }
}
