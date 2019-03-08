package trafficsimulator.simulator.traci;

import java.util.List;

import trafficsimulator.simulator.exceptions.TraciException;
import trafficsimulator.simulator.output.dto.JsonCoordinates;
import trafficsimulator.simulator.output.dto.JsonShape;
import trafficsimulator.simulator.output.dto.JsonVehicleState;
import trafficsimulator.simulator.statistics.dto.VehicleChangeset;
import trafficsimulator.simulator.traci.dto.PhaseInfo;
import trafficsimulator.simulator.traci.dto.SumoVehicle;
import trafficsimulator.simulator.traci.dto.VehicleInfo;

/**
 * Interface for component responsible for communication with TraCI library
 * TraCI library's purpose is to handle simulation in real time
 * @author Petr Svana
 *
 */
public interface TraciService
{
  /**
   * Initializes the simulation
   * @throws TraciException when some of the traci calls fails
   */
  void startSimulation() throws TraciException;
  
  /**
   * Finishes the simulation
   */
  void stopSimulation();
  
  /**
   * Performs next step of the simulation
   * @throws TraciException when traci call fails
   */
  public void nextStep() throws TraciException;

  /**
   * Gets the current simulation time of simulation
   * @return current simulation time
   * @throws TraciException when traci call fails
   */
  public int getSimulationTime() throws TraciException;
    
  /**
   * Gets current number of vehicles in network plus the
   * current number of vehicles yet to be added to network
   * @return number of vehicles
   * @throws TraciException when traci call fails
   */
  public int getVehicleCount() throws TraciException;

  /**
   * Adds a vehicle to the simulation and sets parameters according to the argument
   * @param vehicle vehicle information
   * @throws TraciException when traci call fails
   */
  public void addVehicle(SumoVehicle vehicle) throws TraciException;
  
  /**
   * Retrieves current vehicle status from the simulation
   * @param vehId vehicle id
   * @return object with vehicle status
   * @throws TraciException when traci call fails
   */
  public JsonVehicleState getVehicleState(String vehId) throws TraciException;

  /**
   * Retrieves current vehicle values used for statistics from the simulation
   * @param vehId vehicle id
   * @return change-set of vehicle statistics
   * @throws TraciException when traci call fails
   */
  public VehicleChangeset getVehicleStatistics(String vehId) throws TraciException;
  
  /**
   * Retrieves basic info about all vehicles present in the simulation
   * @return list of objects describing vehicles
   * @throws TraciException when traci call fails
   */
  public List<VehicleInfo> getAlreadyExistingVehicles() throws TraciException;
  
  /**
   * Retrieves basic info about vehicles loaded to the simulation in previous step
   * @return list of objects describing vehicles
   * @throws TraciException when traci call fails
   */
  public List<VehicleInfo> getNewVehicles() throws TraciException;
  
  /**
   * Retrieves list of id for every vehicle in simulation
   * @return list with vehicle id
   * @throws TraciException when traci call fails
   */
  public List<String> getVehicleIds() throws TraciException;
    
  /**
   * Performs check of the detector if vehicle was present on it in previous step
   * @param detectorId detector to check
   * @param laneId lane where is the detector
   * @return true if the detector is occupied; false otherwise
   * @throws TraciException when traci call fails
   */
  public boolean detectorIsOccupied(String detectorId, int laneId) throws TraciException;

  /**
   * Gets id of the current phase on intersection
   * @param intersectionId id of the intersection
   * @return id of the current phase
   * @throws TraciException when traci call fails
   */
  public int getTlsPhaseId(int intersectionId) throws TraciException;

  /**
   * Gets id of the current signal program on the intersection
   * @param intersectionId id of the intersection
   * @return id of the current signal program
   * @throws TraciException when traci call fails
   */
  public String getTlsSignalProgramId(int intersectionId) throws TraciException;

  /**
   * Retrieves list of lanes which are controlled by the intersection
   * @param intersectionId id of the intersection
   * @return list of lane ids
   * @throws TraciException when traci call fails
   */
  public List<String> getControledLanes(int intersectionId) throws TraciException;

  /**
   * Gets information about the current phase on intersection
   * @param intersectionId id of the intersection
   * @param signalProgramId id of the signal program
   * @return information about the phase
   * @throws TraciException when traci call fails
   */
  public PhaseInfo getCurrentIntersectionPhaseInfo(int intersectionId, String signalProgramId) throws TraciException;

  /**
   * Updates the duration of the current phase
   * @param intersectionId id of the intersection
   * @param newDuration [ms] of the new duration
   * @throws TraciException when traci call fails
   */
  public void setCurrentPhaseDuration(int intersectionId, int newDuration) throws TraciException;
  
  /**
   * Retrieves the 'ggGGrryy' kind of string which represents current signals on intersection
   * @param tlsId id of the intersection
   * @return the state string
   * @throws TraciException when traci call fails
   */
  public String getTLStateString(String tlsId) throws TraciException;

  /**
   * Retrieves list of ids for every intersection in simulation
   * @return list with intersection ids
   * @throws TraciException when traci call fails
   */
  public List<String> getIntersectionIds() throws TraciException;
  
  /**
   * Returns simulation time of the next phase switch of an intersection
   * @param tlsId id of the intersection
   * @return simulation time of next switch [ms]
   * @throws TraciException when traci call fails
   */
  public int getNextSwitch(String tlsId) throws TraciException;
  
  /**
   * Performs set up of a different signal program
   * @param tlsId id of the intersection
   * @param programId id of a signal program to be set up
   * @throws TraciException when traci call fails
   */
  public void setSignalProgram(String tlsId, String programId) throws TraciException;
  
  /**
   * Retrieves shape of the intersection as it is rendered in sumo
   * @param tlsId id of the intersection
   * @return object with set of coordinates
   * @throws TraciException when traci call fails
   */
  public JsonShape getIntersectionShape(String tlsId) throws TraciException;
  
  /**
   * Retrieves shape of a lane as it is rendered in sumo
   * @param laneId id of the lane
   * @return object with set of coordinates
   * @throws TraciException when traci call fails
   */
  public JsonShape getLaneShape(String laneId) throws TraciException;
  
  /**
   * Retrieves length of a lane as it is rendered in sumo
   * @param laneId id of the lane
   * @return length of the lane [m]
   * @throws TraciException when traci call fails
   */
  public double getLaneLength(String laneId) throws TraciException;
  
  /**
   * Retrieves the lower left and the upper right corner of the bounding box of the simulation network.
   * @return coordinates of the described point
   * @throws TraciException when traci call fails
   */
  public JsonCoordinates getNetworkBoundary() throws TraciException;

}
