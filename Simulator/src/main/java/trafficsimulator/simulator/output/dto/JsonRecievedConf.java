package trafficsimulator.simulator.output.dto;

import lombok.Data;
import trafficsimulator.shared.enumerators.Direction;

@Data
public class JsonRecievedConf
{
  private boolean firstIntersection;
  private int selectedIntersectionId;
  private String position;
  private int angle;
  private String conf;
  private boolean carsAllowed;
  private boolean vanAllowed;
  private boolean busPublicAllowed;
  private boolean busPrivateAllowed;
  private boolean trucksAllowed;

  
  public boolean isFirstIntersection()
  {
    return firstIntersection;
  }
  
  public Direction getPosition()
  {
    return Direction.fromKey(position)
      .orElseThrow(() -> new IllegalStateException("Uknown direction in client response"));
  }


}