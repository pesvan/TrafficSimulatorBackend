package trafficsimulator.simulator.output.dto;

import lombok.Getter;
import trafficsimulator.shared.dto.Coordinates;

public class JsonVehicleState
{

  @Getter
  private final String id;

  @Getter
  private final JsonCoordinates coords;

  @Getter
  private final int angle;

  @Getter
  private final String signaling;  
  
  @Getter
  private final long speed;
  
  @Getter
  private final long distance;
  
  @Getter
  private final double waitingTime;

  public JsonVehicleState(String id, Coordinates coords, double angle, int signaling, long speed, long distance, double waitingTime)
  {
    this.id = id;
    this.coords = JsonHelper.roundCoordinates(coords);
    this.angle = (int)angle;
    this.signaling = Integer.toBinaryString(signaling);
    this.speed = speed;
    this.distance = distance;
    this.waitingTime = waitingTime;
  }
}
