package trafficsimulator.simulator.traci.dto;

import lombok.Data;

/**
 * @author z003ru0y
 * DTO for vehicles from simulation
 */
@Data
public class VehicleInfo
{
  private final String id; 
  private final double vehLength;
  private final double vehWidth;
  private final String hexColor;
}
