package trafficsimulator.simulator.output.dto;

import lombok.Data;

@Data
public class JsonConnectionLeg
{
  private final String id;
  private final String leg1Id;
  private final String leg2Id;
  
  private final JsonShape shape;
}