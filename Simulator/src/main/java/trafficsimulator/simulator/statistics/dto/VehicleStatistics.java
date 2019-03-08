package trafficsimulator.simulator.statistics.dto;

import trafficsimulator.simulator.output.dto.JsonVehicleStatistics;

/**
 * Data object holding data about specific vehicle for whole duration of simulation
 * @author Petr Svana
 *
 */
public class VehicleStatistics
{
  private String vehicleId;

  /* total footprint of a vehicle */
  private Double vehicleTotalCO2;
  private Double vehicleTotalCO;
  private Double vehicleTotalHC;
  private Double vehicleTotalPMx;
  private Double vehicleTotalNOx;
  private Double vehicleTotalFuelConsumption;

  /** total waiting time of a vehicle in simulation */
  private Double vehicleWaitingTime;
  
  /** total steps in which was vehicle present in simulation */
  private Double stepsInSimulation;

  /**
   * @param id id of the vehicle
   */
  public VehicleStatistics(String id)
  {
    this.vehicleId = id;    
    vehicleTotalCO2 = 0.0;
    vehicleTotalCO = 0.0;
    vehicleTotalHC = 0.0;
    vehicleTotalPMx = 0.0;
    vehicleTotalNOx = 0.0;
    vehicleTotalFuelConsumption = 0.0;    
    vehicleWaitingTime = 0.0;    
    stepsInSimulation = 0.0;

  }

  /**
   * @param changes changeset of the vehicle
   */
  public void update(VehicleChangeset changes)
  {
    vehicleTotalCO2 += changes.getVehicleCO2();
    vehicleTotalCO += changes.getVehicleCO();
    vehicleTotalHC += changes.getVehicleHC();
    vehicleTotalPMx += changes.getVehiclePMx();
    vehicleTotalNOx += changes.getVehicleNOx();
    vehicleTotalFuelConsumption += changes.getVehicleFuelConsumption();
    vehicleWaitingTime += changes.getWaitingTime();
    stepsInSimulation++;
  }

  /**
   * @return json data object of vehicle data per simulation
   */
  public JsonVehicleStatistics generateVehicleResult()
  {
    return new JsonVehicleStatistics(
      vehicleId, stepsInSimulation, vehicleTotalCO2, vehicleTotalCO,
      vehicleTotalHC, vehicleTotalPMx, vehicleTotalNOx,
      vehicleTotalFuelConsumption, vehicleWaitingTime);
  }
}
