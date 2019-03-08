package trafficsimulator.simulator.interfaces;

import java.util.List;

import trafficsimulator.shared.exceptions.InfrastructureElementNotFoundException;
import trafficsimulator.simulator.exceptions.TraciException;
import trafficsimulator.simulator.output.dto.JsonSimulationStep;
import trafficsimulator.simulator.output.dto.JsonSituationLayout;
import trafficsimulator.simulator.output.dto.JsonStatistics;

/**
 * Interface for outer communication with simulator
 * @author z003ru0y
 *
 */

public interface SimulationControl
{
  /**
   * Does required number of steps in the simulation
   * @param numberOfSteps number of steps to do
   * @param visualizationMultiplier how to multiply vehicle dimensions for web
   * @return json object of data for visualization
   * @throws TraciException when the traci call fails
   * @throws InfrastructureElementNotFoundException when some of required infrastracure is missing
   */
  List<JsonSimulationStep> performStepSequence(int numberOfSteps, int visualizationMultiplier) 
      throws TraciException, InfrastructureElementNotFoundException;
  
  /**
   * Correctly stops the simulation
   */
  void stopSimulation();
  
  /**
   * Generates statistics from the start of simulation to the current state
   * @return json object with statistical data
   */
  JsonStatistics getnerateStatistics();
  
  /**
   * Generates map data of the simulation network
   * @param visualizationMultiplier multiplier for vehicle dimensions
   * @return json object with map data
   * @throws TraciException when the traci call fails
   */
  JsonSituationLayout generateMapData(int visualizationMultiplier) throws TraciException;
  
  /**
   * Sets the rate for generating vehicles
   * @param genMod rate
   */
  void setVehicleGenerationRate(int genMod);
  
  /**
   * Dispatches an emergency vehicle to the simulation at given simulation time
   * @param simulationTime simulation time
   * @throws TraciException when the traci call fails
   */
  void dispatchEmergencyVehicle(int simulationTime) throws TraciException;
  
  /**
   * Sets other predefined signal program to the intersection
   * @param intersectionId intersection id
   * @param signalProgramId signal program id
   * @throws TraciException when the traci call fails
   */
  void setSignalProgramToIntersection(int intersectionId, String signalProgramId) throws TraciException;
}
