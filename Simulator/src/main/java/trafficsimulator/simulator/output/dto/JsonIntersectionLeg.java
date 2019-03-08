package trafficsimulator.simulator.output.dto;

import java.util.List;

import lombok.Data;

@Data
public class JsonIntersectionLeg
{
  private final String id;
  private final int angle;
  private final List<JsonLane> laneList;
}
