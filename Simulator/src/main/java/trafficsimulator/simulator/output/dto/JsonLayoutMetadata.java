package trafficsimulator.simulator.output.dto;

import lombok.Data;

@Data
public class JsonLayoutMetadata
{
  private final double simulationStepLength;
  private final int intersectionCount;
  private final int routesCount;
  private final JsonCoordinates networkBoundary;
}
