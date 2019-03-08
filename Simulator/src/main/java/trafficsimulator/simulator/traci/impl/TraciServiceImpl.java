package trafficsimulator.simulator.traci.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tudresden.sumo.cmd.Inductionloop;
import de.tudresden.sumo.cmd.Junction;
import de.tudresden.sumo.cmd.Lane;
import de.tudresden.sumo.cmd.Simulation;
import de.tudresden.sumo.cmd.Trafficlight;
import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.ws.container.SumoBoundingBox;
import de.tudresden.ws.container.SumoColor;
import de.tudresden.ws.container.SumoGeometry;
import de.tudresden.ws.container.SumoPosition2D;
import de.tudresden.ws.container.SumoStringList;
import de.tudresden.ws.container.SumoTLSController;
import de.tudresden.ws.container.SumoTLSPhase;
import de.tudresden.ws.container.SumoTLSProgram;
import it.polito.appeal.traci.SumoTraciConnection;
import trafficsimulator.shared.dto.Coordinates;
import trafficsimulator.simulator.exceptions.TraciException;
import trafficsimulator.simulator.output.dto.JsonCoordinates;
import trafficsimulator.simulator.output.dto.JsonHelper;
import trafficsimulator.simulator.output.dto.JsonShape;
import trafficsimulator.simulator.output.dto.JsonVehicleState;
import trafficsimulator.simulator.statistics.dto.VehicleChangeset;
import trafficsimulator.simulator.traci.TraciService;
import trafficsimulator.simulator.traci.dto.PhaseInfo;
import trafficsimulator.simulator.traci.dto.SumoVehicle;
import trafficsimulator.simulator.traci.dto.VehicleInfo;

/**
 * @author z003ru0y
 *
 */
public class TraciServiceImpl implements TraciService
{
  private SumoTraciConnection traciConnection;

  private final double sumoStepLength;

  private final String sumoConfPath;

  private final String sumoConfFileName;
  
  private static Logger logger = LoggerFactory.getLogger(TraciServiceImpl.class);  

  /**
   * @param sumoConfPath path to SUMO configuration files
   * @param sumoStepLength simulation step length
   * @param sumoConfFileName name of main sumo configuration file
   */
  public TraciServiceImpl(String sumoConfPath, double sumoStepLength, String sumoConfFileName)
  {
    this.sumoConfPath = sumoConfPath;
    this.sumoStepLength = sumoStepLength;
    this.sumoConfFileName = sumoConfFileName;
    
    logger.debug("Traci Service started");
  }

  public void startSimulation() throws TraciException
  {
    String sumoBinary = "sumo";
    traciConnection = new SumoTraciConnection(sumoBinary, sumoConfPath + sumoConfFileName);
    traciConnection.addOption("step-length", String.valueOf(sumoStepLength));
    traciConnection.addOption("collision.action", "warn");

    try
    {
      traciConnection.runServer();
    }
    catch (IOException e)
    {
      throw new TraciException(e, traciConnection, "Could not start the simulation");
    }
    
    logger.debug("Traci simulation started");
  }

  public void stopSimulation()
  {
    traciConnection.close();
  }

  public void nextStep() throws TraciException
  {
    try
    {
      traciConnection.do_timestep();
    }
    catch (Exception e)
    {
      throw new TraciException(e, traciConnection, "Could not perform next step");
    }
  }

  public int getSimulationTime() throws TraciException
  {
    try
    {
      return (int) traciConnection.do_job_get(Simulation.getCurrentTime());
    }
    catch (Exception e)
    {
      throw new TraciException(e, traciConnection, "Could not get current simulation time");
    }
  }

  public int getVehicleCount() throws TraciException
  {
    try
    {
      return (int) traciConnection.do_job_get(Simulation.getMinExpectedNumber());
    }
    catch (Exception e)
    {
      throw new TraciException(e, traciConnection, "Could not get current vehicle count");
    }
  }

  public void addVehicle(SumoVehicle vehicle) throws TraciException
  {
    try
    {      
      traciConnection.do_job_set(Vehicle.add(vehicle.getId(), "DEFAULT_VEHTYPE", vehicle.getRoute(), vehicle.getSimtime(), 0.0, 0.0, (byte) 0));
      traciConnection.do_job_set(Vehicle.setVehicleClass(vehicle.getId(), vehicle.getVehicleClass()));
      traciConnection.do_job_set(Vehicle.setShapeClass(vehicle.getId(), vehicle.getShapeClass()));
      traciConnection.do_job_set(Vehicle.setLength(vehicle.getId(), vehicle.getVehicleType().getLength()));
      traciConnection.do_job_set(Vehicle.setColor(vehicle.getId(), vehicle.getColor()));
      traciConnection.do_job_set(Vehicle.setAccel(vehicle.getId(), vehicle.getVehicleType().getAccel()));
      traciConnection.do_job_set(Vehicle.setDecel(vehicle.getId(), vehicle.getVehicleType().getDecel()));
      traciConnection.do_job_set(Vehicle.setTau(vehicle.getId(), 2.0));
    }
    catch (Exception e)
    {
      throw new TraciException(e, traciConnection, "Could not set up new vehicle");
    }

  }

  public List<VehicleInfo> getAlreadyExistingVehicles() throws TraciException
  {
    List<VehicleInfo> existing = new ArrayList<>(); 

    SumoStringList existingIds;
    try
    {
      existingIds = (SumoStringList) traciConnection.do_job_get(Vehicle.getIDList());
      for (int i = 0; i < existingIds.size(); i++)
      {
        existing.add(getVehicleInfo(existingIds.get(i)));
      }

      return existing;
    }
    catch (Exception e)
    {
      throw new TraciException(e, traciConnection, "Could not get information about current vehicles in database");
    }
  }

  public List<VehicleInfo> getNewVehicles() throws TraciException
  {
    List<VehicleInfo> newVehicles = new ArrayList<>();

    SumoStringList newIds;
    try
    {
      newIds = (SumoStringList) traciConnection.do_job_get(Simulation.getLoadedIDList());
      for (int i = 0; i < newIds.size(); i++)
      {
        newVehicles.add(getVehicleInfo(newIds.get(i)));
      }

      return newVehicles;
    }
    catch (Exception e)
    {
      throw new TraciException(e, traciConnection, "Could not get information about new vehicles in database");
    }
  }

  public List<String> getVehicleIds() throws TraciException
  {
    try
    {
      return (SumoStringList) traciConnection.do_job_get(Vehicle.getIDList());
    }
    catch (Exception e)
    {
      throw new TraciException(e, traciConnection, "Could not get list of vehicle ids");
    }
  }

  public boolean detectorIsOccupied(String detectorId, int laneId) throws TraciException
  {
    int occupancy;
    try
    {
      occupancy = (int) traciConnection.do_job_get(Inductionloop.getLastStepVehicleNumber(detectorId + "_" + laneId));
      return occupancy > 0;
    }
    catch (Exception e)
    {
      throw new TraciException(e, traciConnection, "Could not check detector occupancy");
    }
  }

  public int getTlsPhaseId(int intersectionId) throws TraciException
  {
    try
    {
      return (int) traciConnection.do_job_get(Trafficlight.getPhase(intersectionId + ""));
    }
    catch (Exception e)
    {
      throw new TraciException(e, traciConnection, "Could not get intersection phase id");
    }
  }

  public String getTlsSignalProgramId(int intersectionId) throws TraciException
  {
    try
    {
      return (String) traciConnection.do_job_get(Trafficlight.getProgram(intersectionId + ""));
    }
    catch (Exception e)
    {
      throw new TraciException(e, traciConnection, "Could not get intersection signal program id");
    }
  }

  public PhaseInfo getCurrentIntersectionPhaseInfo(int intersectionId, String signalProgramId) throws TraciException
  {
    SumoTLSPhase currentPhase = this.getCurrentIntersectionPhase(intersectionId, signalProgramId);
    return new PhaseInfo(currentPhase.phasedef, currentPhase.duration / 1000.0);
  }

  private SumoTLSPhase getCurrentIntersectionPhase(int intersectionId, String signalProgramId) throws TraciException
  {
    SumoTLSController controller = this.getTlsController(intersectionId);
    HashMap<String, SumoTLSProgram> phasesmap = controller.programs;

    SumoTLSProgram program = phasesmap.get(signalProgramId);
    List<SumoTLSPhase> phaseList = program.phases;
    return phaseList.get(program.currentPhaseIndex);
  }

  private SumoTLSController getTlsController(int intersectionId) throws TraciException
  {
    try
    {
      return (SumoTLSController) traciConnection.do_job_get(Trafficlight.getCompleteRedYellowGreenDefinition(intersectionId + ""));
    }
    catch (Exception e)
    {
      throw new TraciException(e, traciConnection, "Could not get intersection info");
    }
  }

  public void setCurrentPhaseDuration(int intersectionId, int newDuration) throws TraciException
  {
    try
    {
      traciConnection.do_job_set(Trafficlight.setPhaseDuration(intersectionId + "", newDuration));
    }
    catch (Exception e)
    {
      throw new TraciException(e, traciConnection, "Could not set phase duration");
    }
  }

  // retrieval
  public JsonVehicleState getVehicleState(String vehId) throws TraciException
  {
    try
    {
      SumoPosition2D result = (SumoPosition2D) traciConnection.do_job_get(Vehicle.getPosition(vehId));
      double resultAngle = (double) traciConnection.do_job_get(Vehicle.getAngle(vehId));
      int signaling = (int) traciConnection.do_job_get(Vehicle.getSignals(vehId));
      Coordinates vehicleCoordinates = new Coordinates(result.x, result.y);
      long speed = Math.round((double) traciConnection.do_job_get(Vehicle.getSpeed(vehId)) * 3.6);
      long distance = Math.round((double) traciConnection.do_job_get(Vehicle.getDistance(vehId)));
      double waitingTime = (double) traciConnection.do_job_get(Vehicle.getWaitingTime(vehId));

      return new JsonVehicleState(vehId, vehicleCoordinates, resultAngle, signaling, speed, distance, waitingTime);
    }
    catch (Exception e)
    {
      throw new TraciException(e, traciConnection, "Could not get vehicle state");
    }
  }

  public VehicleChangeset getVehicleStatistics(String vehId) throws TraciException
  {
    try
    {
      double CO2 = (double) traciConnection.do_job_get(Vehicle.getCO2Emission(vehId));
      double CO = (double) traciConnection.do_job_get(Vehicle.getCOEmission(vehId));
      double HC = (double) traciConnection.do_job_get(Vehicle.getHCEmission(vehId));
      double PMx = (double) traciConnection.do_job_get(Vehicle.getPMxEmission(vehId));
      double NOx = (double) traciConnection.do_job_get(Vehicle.getNOxEmission(vehId));
      double fuel = (double) traciConnection.do_job_get(Vehicle.getFuelConsumption(vehId));
      double waiting = (double) traciConnection.do_job_get(Vehicle.getWaitingTime(vehId));

      return new VehicleChangeset(CO2, CO, HC, PMx, NOx, fuel, waiting);
    }
    catch (Exception e)
    {
      throw new TraciException(e, traciConnection, "Could not get vehicle statistics");
    }
  }

  private VehicleInfo getVehicleInfo(String vehId) throws TraciException
  {
    try
    {
      double length = (double) traciConnection.do_job_get(Vehicle.getLength(vehId));
      double width = (double) traciConnection.do_job_get(Vehicle.getWidth(vehId));
      SumoColor color = (SumoColor) traciConnection.do_job_get(Vehicle.getColor(vehId));
      String hexColor = String.format("#%02X%02X%02X", color.r, color.g, color.b);

      return new VehicleInfo(vehId, length, width, hexColor);
    }
    catch (Exception e)
    {
      throw new TraciException(e, traciConnection, "Could not get vehicle information");
    }
  }

  public String getTLStateString(String tlsId) throws TraciException
  {
    try
    {
      return (String) traciConnection.do_job_get(Trafficlight.getRedYellowGreenState(tlsId));
    }
    catch (Exception e)
    {
      throw new TraciException(e, traciConnection, "Could not get intersection signal string");
    }
  }

  public List<String> getIntersectionIds() throws TraciException
  {
    try
    {
      return (SumoStringList) traciConnection.do_job_get(Trafficlight.getIDList());
    }
    catch (Exception e)
    {
      throw new TraciException(e, traciConnection, "Could not get intersection id list");
    }
  }

  public int getNextSwitch(String tlsId) throws TraciException
  {
    try
    {
      return (int) traciConnection.do_job_get(Trafficlight.getNextSwitch(tlsId));
    }
    catch (Exception e)
    {
      throw new TraciException(e, traciConnection, "Could not get time of the next switch");
    }
  }

  // TODO: this methods needs to be rewritten
  public List<String> getControledLanes(int intersectionId) throws TraciException
  {
    try
    {
      return (SumoStringList) traciConnection.do_job_get(Trafficlight.getControlledLanes(intersectionId + ""));
    }
    catch (Exception e)
    {
      throw new TraciException(e, traciConnection, "Could not get list of the controlled lanes");
    }
  }

  public void setSignalProgram(String tlsId, String programId) throws TraciException
  {
    try
    {
      traciConnection.do_job_set(Trafficlight.setProgram(tlsId, programId));
    }
    catch (Exception e)
    {
      throw new TraciException(e, traciConnection, "Could not set signal program");
    }
  }

  public JsonShape getIntersectionShape(String tlsId) throws TraciException
  {
    List<JsonCoordinates> coords = new ArrayList<>();

    SumoGeometry geom;
    try
    {
      geom = (SumoGeometry) traciConnection.do_job_get(Junction.getShape(tlsId));
      for (SumoPosition2D position : geom.coords)
      {
        Coordinates coordinates = new Coordinates(position.x, position.y);
        coords.add(JsonHelper.roundCoordinates(coordinates));
      }

      return new JsonShape(coords);
    }
    catch (Exception e)
    {
      throw new TraciException(e, traciConnection, "Could not get shape of intersection");
    }
  }

  public JsonShape getLaneShape(String laneId) throws TraciException
  {
    List<JsonCoordinates> coords = new ArrayList<>();

    SumoGeometry geom;
    try
    {
      geom = (SumoGeometry) traciConnection.do_job_get(Lane.getShape(laneId));
      for (SumoPosition2D position : geom.coords)
      {
        Coordinates coordinates = new Coordinates(position.x, position.y);
        coords.add(JsonHelper.roundCoordinates(coordinates));
      }
      return new JsonShape(coords);
    }
    catch (Exception e)
    {
      throw new TraciException(e, traciConnection, "Could not get shape of lane");
    }
  }

  public double getLaneLength(String laneId) throws TraciException
  {
    try
    {
      return JsonHelper.roundDouble((double) traciConnection.do_job_get(Lane.getLength(laneId)));
    }
    catch (Exception e)
    {
      throw new TraciException(e, traciConnection, "Could not get length of lane");
    }
  }

  public JsonCoordinates getNetworkBoundary() throws TraciException
  {
    SumoBoundingBox boundingBox;
    try
    {
      boundingBox = (SumoBoundingBox) traciConnection.do_job_get(Simulation.getNetBoundary());
      return JsonHelper.roundCoordinates(new Coordinates(boundingBox.x_max, boundingBox.y_max));
    }
    catch (Exception e)
    {
      throw new TraciException(e, traciConnection, "Could not get network boundary");
    }
  }
}
