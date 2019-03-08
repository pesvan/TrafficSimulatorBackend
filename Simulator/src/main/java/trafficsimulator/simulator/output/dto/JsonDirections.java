package trafficsimulator.simulator.output.dto;

import lombok.Data;

@Data
public class JsonDirections
{
  private final boolean left;
  private final boolean right;
  private final boolean straight;
}
