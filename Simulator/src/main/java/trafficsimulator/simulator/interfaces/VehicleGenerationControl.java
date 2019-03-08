package trafficsimulator.simulator.interfaces;

import java.util.List;

import trafficsimulator.simulator.exceptions.TraciException;
import trafficsimulator.simulator.output.dto.JsonVehicle;

/**
 * Interface responsible for controlling vehicle operations from the simulation control
 * @author z003ru0y
 */
public interface VehicleGenerationControl
{

  /**
   * Changes the generation rate of new vehicles
   * @param genMod rate of vehicles generated
   */
  void setVehicleGenerationRate(int genMod);

  /**
   * Sends a emergency vehicle to the simulation
   * @param simulationTime time when to dispatch the vehicle
   * @throws TraciException when some of the traci calls fails
   */
  void dispatchEmergencyVehicle(int simulationTime) throws TraciException;

  /**
   * Generate vehicles for the current simulation step and returns their basic information
   * @param simulationTime when to insert the vehicles
   * @param statistics output parameter to gather statistical data
   * @param visualizationMultiplier multiplier for vehicle dimensions
   * @return list of objects containing information about newly added vehicles
   * @throws TraciException when some of the traci calls fails
   */
  List<JsonVehicle> generateVehicles(int simulationTime, StatisticsControl statistics, int visualizationMultiplier) throws TraciException;
}
