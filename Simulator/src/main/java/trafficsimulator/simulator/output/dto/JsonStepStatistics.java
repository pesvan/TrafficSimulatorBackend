package trafficsimulator.simulator.output.dto;

import lombok.Data;

@Data
public class JsonStepStatistics
{
  private final Double simTime;
  
  private final Double stepTotalCO2;
  private final Double stepTotalCO;
  private final Double stepTotalHC;
  private final Double stepTotalPMx;
  private final Double stepTotalNOx;
  private final Double stepTotalFuelConsumption;
  
  private final int vehicleCount;
}
