package trafficsimulator.shared.dto;

import lombok.Data;

/**
 * DTO for connections between 2 intersections
 * @author z003ru0y
 *
 */
@Data
public class IntersectionConnection
{
  private final String id;  
  private final Intersection intersection1;
  private final Intersection intersection2;
  private final Leg legI1;
  private final Leg legI2;
  
  
  /**
   * @return retrieves back id for the SUMO
   */
  public String getIdBack()
  {
    return id + "-back";
  }
}
