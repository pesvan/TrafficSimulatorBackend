package trafficsimulator.simulator.output.dto;

import lombok.Data;

@Data
public class JsonVehicleStatistics
{
  private final String id;
  private final double timeInSimulation;
  private final double vehicleTotalCO2;
  private final double vehicleTotalCO;
  private final double vehicleTotalHC;
  private final double vehicleTotalPMx;
  private final double vehicleTotalNOx;
  private final double vehicleTotalFuelConsumption;
  private final double vehicleWaitingTime;
}
