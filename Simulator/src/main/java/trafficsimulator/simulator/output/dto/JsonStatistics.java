package trafficsimulator.simulator.output.dto;

import java.util.List;

import lombok.Data;

@Data
public class JsonStatistics
{
  private final List<JsonVehicleStatistics> vehicles;
  private final List<JsonStepStatistics> steps;
  
  private final double simulationTime;
  private final double simulationStepLength;
  private final int totalVehiclesAdded;
  
  private final double totalCO2;
  private final double totalCO;
  private final double totalHC;
  private final double totalPMx;
  private final double totalNOx;
  private final double totalFuelConsumption;
  
  private final double averageVehicleTimeInSimulation;
  private final double averageVehicleWaitingTime;
  
  
  //Chart data
  private final double[] simSteps;
  
  private final int[] vehiclesInTime;
  private final double[] COInTime;
  private final double[] CO2InTime;
  private final double[] NOxInTime;
  private final double[] PMxInTime;
  private final double[] HCInTime;
  private final double[] FuelInTime;

}
