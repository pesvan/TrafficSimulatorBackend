package trafficsimulator.simulator.interfaces;

import trafficsimulator.simulator.output.dto.JsonStatistics;
import trafficsimulator.simulator.statistics.dto.VehicleChangeset;

/**
 * Interface responsible for saving adn retrieving statistical data of simulation
 * @author z003ru0y
 *
 */
public interface StatisticsControl
{
  /**
   * Sets current simulation time to the statistics module
   * @param simulationTime simulation time
   */
  void setSimulationTime(double simulationTime);
  
  /**
   * Adds new vehicle to the statistics
   * @param id of the vehicle
   */
  void addVehicle(String id);
  
  /**
   * Updates existing vehicle in the statistics
   * @param id of the vehicle
   * @param changes changeset object
   */
  void updateVehicle(String id, VehicleChangeset changes);
  
  /**
   * Generates json object with statistics
   * @return json object with statistics
   */
  JsonStatistics generateStatistics();
}
