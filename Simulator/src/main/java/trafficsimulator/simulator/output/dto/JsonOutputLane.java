package trafficsimulator.simulator.output.dto;

import lombok.Data;

@Data
public class JsonOutputLane
{
  private final String id;  
  private final JsonShape shape;
  private final double laneLength;

}
