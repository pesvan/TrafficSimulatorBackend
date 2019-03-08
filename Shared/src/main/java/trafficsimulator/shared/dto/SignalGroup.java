package trafficsimulator.shared.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for signal groups
 * @author z003ru0y
 *
 */
public class SignalGroup
{
  
  @Getter
  private final int id;
  
  @Getter
  private Directions directions;
  
  @Getter
  private final SgSignalisation signalisation;

  @Getter
  @Setter
  private Map<Integer, Sequence> sequenceList;
  
  @Getter
  private List<Detector> detectors;
  
  @Getter
  private List<Lane> lanes;

  /**
   * @param id signal group id
   * @param directions signal group directions
   * @param tnGnRd green to red time
   * @param tnRdGn red to green time
   */
  public SignalGroup(int id, String directions, int tnGnRd, int tnRdGn)
  {
    this.id = id;
    this.directions = new Directions();
    this.signalisation = new SgSignalisation(tnGnRd, tnRdGn);
    this.detectors = new ArrayList<>();
    this.lanes = new ArrayList<>();
  }

  /**
   * @param signalProgramId signal program id
   * @return retrieves sequence list of this signal group based on signal program id
   */
  public Sequence getSequenceBySPId(int signalProgramId)
  {
    return getSequenceList().get(signalProgramId);
  }
  
  /**
   * Adds detector to this signal group
   * @param detector detector
   */
  public void addDetector(Detector detector)
  {
    detectors.add(detector);
  }

  /**
   * Adds lane controlled by this signal group
   * @param lane lane
   */
  public void addLane(Lane lane)
  {
    lanes.add(lane);
    updateDirectionsByLanes();
    sortLanesByDirection();
  }
  
  /**
   * Changes direction of the lane and updates this signal group directions as well
   * @param lane lane
   * @param newDirections new lane directions
   * @param originLeg leg of the lane
   */
  public void changeLane(Lane lane, Directions newDirections, Leg originLeg)
  {
    lane.changeLane(newDirections, originLeg);
    updateDirectionsByLanes();
    sortLanesByDirection();
  }
  
  /**
   * Deletes lane and updates this signal group directions as well
   * @param lane to be deleted
   */
  public void deleteLane(Lane lane)
  {
    lanes.remove(lane);
    updateDirectionsByLanes();
    sortLanesByDirection();
  }
  
  /**
   * @return retrieves id for the next lane
   */
  public int getNextLaneId()
  {
    int maxId = 0;
    
    for (Lane lane : lanes)
    {
      if (lane.getId() > maxId)
      {
        maxId = lane.getId();
      }
    }
    
    return maxId+1;
  }
  
  private void updateDirectionsByLanes()
  {
    Directions newDirections = new Directions();
    
    for (Lane lane : lanes)
    {
      if (lane.getDirections().isLeft())
      {
        newDirections.setLeft(true);
      }
      if (lane.getDirections().isRight())
      {
        newDirections.setRight(true);
      }
      if (lane.getDirections().isStraight())
      {
        newDirections.setStraight(true);
      }
    }
    
    this.directions = newDirections;
  }
  
  private void sortLanesByDirection()
  {
    Collections.sort(lanes);
    for (int i = lanes.size()-1, j = 0 ; i >= 0; i--, j++)
    {
      lanes.get(i).setId(j);
    }
  }

  @Override
  public String toString()
  {
    return id
      + ", Directions "
      + directions.toString()
      + ", Signalisation "
      + signalisation.toString();
  }

  @Override
  public boolean equals(Object o)
  {
    SignalGroup obj = (SignalGroup)o;
    return obj.getId() == id;
  }

}
