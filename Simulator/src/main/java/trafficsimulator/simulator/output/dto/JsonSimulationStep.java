package trafficsimulator.simulator.output.dto;

import java.util.List;

import lombok.Data;

@Data
public class JsonSimulationStep
{
  
  private final int simulationStep;
  private final List<JsonVehicleState> vehicleState;
  private final List<JsonTrafficLightState> tlState;
  private final List<JsonPhaseState> phaseState;
  private final List<JsonVehicle> vehiclesToAdd;
}
