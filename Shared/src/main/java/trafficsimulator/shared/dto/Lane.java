package trafficsimulator.shared.dto;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.Setter;

/**
 * One of the more complicated classes is Lane
 * @author z003ru0y
 *
 */
public class Lane implements Comparable<Lane>
{
  private static Logger log = LoggerFactory.getLogger(Lane.class);


  @Getter
  private int id;
  
  @Getter
  private String laneNetId;
  
  @Getter
  @Setter
  private Directions directions;
  
  @Getter
  private List<Leg> outputLegs;

  /**
   * @param id id of a lane
   * @param directions lane directions
   * @param laneNetId lane id for the SUMO network configuration file
   */
  public Lane(int id, Directions directions, String laneNetId)
  {
    this.id = id;
    this.directions = directions;
    this.laneNetId = laneNetId;
  }
  
  /**
   * @param newId sets the lane id
   */
  public void setId(int newId)
  {
    id = newId;
    String laneNet = laneNetId.substring(0, laneNetId.indexOf("_")+1);
    
    log.debug("Changing lane id from {} to {}", laneNetId, laneNet + newId);
    
    laneNetId = laneNet + newId;
  }
  
  /**
   * Changes directions of the lane
   * @param newDirections new directions 
   * @param originLeg leg where is the lane
   */
  public void changeLane(Directions newDirections, Leg originLeg)
  {
    setDirections(newDirections);
    calculateOutputLegs(originLeg);
  }
  
  /**
   * Calculates possible turns from the lane based on intersection layout and possible directions
   * @param originLeg leg where is the lane located
   */
  public void calculateOutputLegs(Leg originLeg)
  {
    List<Leg> outputLegs = new ArrayList<>();
    if (directions.isStraight() && originLeg.getStraightLeg() != null)
    {
      outputLegs.add(originLeg.getStraightLeg());
    }
    if (directions.isLeft() && originLeg.getLeftMostLeg() != null)
    {
      outputLegs.add(originLeg.getLeftMostLeg());
    }
    if (directions.isRight() && originLeg.getRightMostLeg() != null)
    {
      outputLegs.add(originLeg.getRightMostLeg());
    }
    if (directions.isBack())
    {
      outputLegs.add(originLeg);
    }
    this.outputLegs = outputLegs;
  }
 
  @Override
  public String toString()
  {
    return "Lane [id=" + id
      + " direction="
      + directions
      + "]";
  }

  @Override
  public int compareTo(Lane anotherLane)
  {
    return this.getDirections().compareTo(anotherLane.getDirections());
  }
}
