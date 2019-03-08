package trafficsimulator.simulator.output.dto;

import lombok.Data;

@Data
public class JsonVehicle
{
  private final String id;
  private final double vehLength;
  private final double vehWidth;
  private final String hexColor;
}
