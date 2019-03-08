package trafficsimulator.simulator.output.dto;

import lombok.Data;

@Data
public class JsonTrafficLightState
{
  private final String laneId;
  private final char laneState;
}
