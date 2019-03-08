package trafficsimulator.shared.dto;

import lombok.Data;

/**
 * DTO for signal program phase
 * @author z003ru0y
 *
 */
@Data
public class Phase implements Comparable<Phase>
{  
  private final int id;  
  private final int minDuration;  
  private final int maxDuration;



  @Override
  public String toString()
  {
    return "Phase " + id
      + " - min: "
      + minDuration
      + ", max: "
      + maxDuration;
  }

  @Override
  public int compareTo(Phase o)
  {
    return 0;
  }

}
