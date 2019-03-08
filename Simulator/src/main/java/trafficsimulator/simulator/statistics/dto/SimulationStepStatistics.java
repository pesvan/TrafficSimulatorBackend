package trafficsimulator.simulator.statistics.dto;

import trafficsimulator.simulator.output.dto.JsonStepStatistics;

/**
 * Object which holds information about one simulation step
 * @author Petr Svana
 */

public class SimulationStepStatistics
{
  private final Double simulationTime;

  /** contribution by this simulation step  */
  private Double stepTotalCO2;
  private Double stepTotalCO;
  private Double stepTotalHC;
  private Double stepTotalPMx;
  private Double stepTotalNOx;
  private Double stepTotalFuelConsumption;
  
  /** vehicles present in simulation at that time */
  private int vehicleCount;

  /**
   * @param simTime simulation time of this data unit
   */
  public SimulationStepStatistics(double simTime)
  {
    this.simulationTime = simTime;
    
    stepTotalCO2 = 0.0;
    stepTotalCO = 0.0;
    stepTotalHC = 0.0;
    stepTotalPMx = 0.0;
    stepTotalNOx = 0.0;
    stepTotalFuelConsumption = 0.0;
    vehicleCount = 0;    
  }

  /**
   * @param changes changeset of vehicle
   */
  public void update(VehicleChangeset changes)
  {
    stepTotalCO2 += changes.getVehicleCO2();
    stepTotalCO += changes.getVehicleCO();
    stepTotalHC += changes.getVehicleHC();
    stepTotalPMx += changes.getVehiclePMx();
    stepTotalNOx += changes.getVehicleNOx();
    stepTotalFuelConsumption += changes.getVehicleFuelConsumption();
    vehicleCount++;
  }

  /**
   * @return json object for simulation step
   */
  public JsonStepStatistics generateStepResult()
  {
    return new JsonStepStatistics(simulationTime, stepTotalCO2, stepTotalCO,
      stepTotalHC, stepTotalPMx, stepTotalNOx, stepTotalFuelConsumption, vehicleCount);
  }
}
