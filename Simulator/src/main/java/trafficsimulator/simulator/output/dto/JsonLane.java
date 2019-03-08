package trafficsimulator.simulator.output.dto;

import lombok.Data;

@Data
public class JsonLane
{
  private final String id;
  private final boolean isInputLane;
  private final JsonDirections directions;
  private final double laneLength;
  private final JsonShape shape;
}
