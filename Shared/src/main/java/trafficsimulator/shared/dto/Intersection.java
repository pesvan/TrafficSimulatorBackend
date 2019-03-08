package trafficsimulator.shared.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.Getter;
import lombok.Setter;
import trafficsimulator.shared.exceptions.InfrastructureElementNotFoundException;

/**
 * More advanced DTO with intersection data and operations
 * @author z003ru0y
 *
 */
public class Intersection
{

  @Getter
  private final int id;
  
  @Getter
  private final List<SignalProgram> signalPrograms;
  
  @Getter
  private List<Leg> legs;
  
  @Getter
  private final Coordinates coordinates;
  
  @Getter
  private final Directions intersectionDirections;
  
  @Getter
  private final GridPosition gridPosition;
  
  @Getter
  private final Angle angle;

  @Getter
  @Setter
  private SignalProgram selectedSignalProgram;  

  /**
   * @param id id of intersection
   * @param coordinates intersection coordinates
   * @param gridPosition intersection grid position
   * @param signalPrograms signal programs
   * @param legs intersection legs
   * @param angle intersection is rotated by this angle
   */
  public Intersection(int id, Coordinates coordinates, Optional<GridPosition> gridPosition, 
    List<SignalProgram> signalPrograms, List<Leg> legs, Angle angle)
  {
    this.id = id;
    this.coordinates = coordinates;
    this.gridPosition = gridPosition.orElseGet(() -> new GridPosition(0, 0));
    this.signalPrograms = signalPrograms;
    sortLegsByAngle(legs);
    //this.legs = legs;
    this.intersectionDirections = setIntersectionDirections();
    this.angle = angle;
    this.setSelectedSignalProgram(signalPrograms.get(0));
  }
  
  /**
   * @return retrieves all the signal groups of the intersections from legs
   */
  public List<SignalGroup> getAllSignalGroups()
  {
    List<SignalGroup> allSignalGroups = new ArrayList<>();
    legs.forEach(leg -> allSignalGroups.addAll(leg.getSignalGroups()));
    return allSignalGroups;
  }
  
  /**
   * @return list of signal program ids
   */
  public List<String> getSignalProgramIds()
  {
    List<String> programIds = new ArrayList<>();
    
    for (SignalProgram sp : getSignalPrograms())
    {
      programIds.add(sp.getProgramId());
    }
    
    return programIds;
  }
  
  /**
   * @return retrieves all the detectors of a intersection
   */
  public List<Detector> getAllDetectors()
  {
    List<Detector> allDetectors = new ArrayList<>();
    getAllSignalGroups().forEach(sg -> allDetectors.addAll(sg.getDetectors()));
    return allDetectors;   
  }

  /**
   * @return retrieves most left X coordinate of the intersection
   */
  public Integer getMostLeftX()
  {
    Integer lowestX = null;
    
    for (Leg leg : legs)
    {
      if (lowestX == null || leg.getCoordinates().getX() < lowestX)
      {
        lowestX = (int) leg.getCoordinates().getX();
      }
    }
    
    return lowestX;
  }
  
  /**
   * @return retrieves most bottom Y coordinate of the intersection
   */
  public Integer getMostDownY()
  {
    Integer lowestY = null;
    
    for (Leg leg : legs)
    {
      if (lowestY == null || leg.getCoordinates().getY() < lowestY)
      {
        lowestY = (int) leg.getCoordinates().getY();
      }
    }
    
    return lowestY;
  }
  
  /**
   * @param id id of a leg
   * @return retrieves leg from a intersection
   * @throws InfrastructureElementNotFoundException when the leg was not found
   */
  public Leg getLegById(String id) throws InfrastructureElementNotFoundException
  {
    for (Leg leg : legs)
    {
      if(leg.getId().equals(id))
      {
        return leg;
      }
    }
    
    throw new InfrastructureElementNotFoundException("Leg by ID: " + id);
  }
  
  private Directions setIntersectionDirections()
  {
    Directions directions = new Directions();
    for (Leg leg : legs)
    {
      if (leg.getAngle().getValue() >= 315 || leg.getAngle().getValue() <= 45)
      {
        directions.setBack(true);
      }
      else if (leg.getAngle().getValue() >= 45 && leg.getAngle().getValue() <= 135)
      {
        directions.setLeft(true);
      }
      else if (leg.getAngle().getValue() >= 135 && leg.getAngle().getValue() <= 225)
      {
        directions.setStraight(true);
      }
      else
      {
        directions.setBack(true);
      }
    }
    return directions;
  }
  
  
  //92 is first, 91 is last
  private void sortLegsByAngle(List<Leg> legs)  
  {
    
    List<Leg> newLegs = new ArrayList<>(); 
    
    for (int i = 0, j = 180; i < 360; i++, j++)
    {
      if (j == 360)
      {
        j = 0;
      }
      Leg leg = getLegByAngle(legs, new Angle(j));
      if( leg != null)
      {
        newLegs.add(leg);
      }
        
    }
    
    this.legs = newLegs;
  }
  
  private Leg getLegByAngle(List<Leg> legs, Angle angle)
  {
    for (Leg leg : legs)
    {
      if (leg.getAngle().equals(angle))
      {
        return leg;
      }
    }
    return null;
  }
  
  @Override
  public String toString()
  {
    return legs.toString() 
      + signalPrograms.toString();
  }

  @Override
  public boolean equals(Object o)
  {
    Intersection otherI = (Intersection)o;
    return this.id == otherI.getId();
  }

}
