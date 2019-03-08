package trafficsimulator.simulator.interfaces.impl;

import trafficsimulator.shared.dto.Situation;
import trafficsimulator.shared.dto.Intersection;
import trafficsimulator.shared.dto.Lane;
import trafficsimulator.shared.dto.Leg;
import trafficsimulator.shared.dto.SignalGroup;
import trafficsimulator.shared.exceptions.InfrastructureElementNotFoundException;
import trafficsimulator.simulator.exceptions.NoIntersectionInSituationException;
import trafficsimulator.simulator.exceptions.NoValidRoutesInSituationException;
import trafficsimulator.simulator.exceptions.TraciException;
import trafficsimulator.simulator.interfaces.JsonMapDataGenerator;
import trafficsimulator.simulator.interfaces.SignalProgramControl;
import trafficsimulator.simulator.interfaces.SimulationControl;
import trafficsimulator.simulator.interfaces.StatisticsControl;
import trafficsimulator.simulator.interfaces.TrafficActuationControl;
import trafficsimulator.simulator.interfaces.VehicleGenerationControl;
import trafficsimulator.simulator.output.dto.JsonPhaseState;
import trafficsimulator.simulator.output.dto.JsonSimulationStep;
import trafficsimulator.simulator.output.dto.JsonSituationLayout;
import trafficsimulator.simulator.output.dto.JsonStatistics;
import trafficsimulator.simulator.output.dto.JsonTrafficLightState;
import trafficsimulator.simulator.output.dto.JsonVehicle;
import trafficsimulator.simulator.output.dto.JsonVehicleState;
import trafficsimulator.simulator.traci.TraciService;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author z003ru0y
 *
 */
public class SimulationControlImpl implements SimulationControl
{
  private static Logger logger = LoggerFactory.getLogger(SimulationControlImpl.class);
  
  private final TraciService traciService;
  
  private VehicleGenerationControl vehicleGenerationControl;
  
  private SignalProgramControl signalProgramControl;
  
  private TrafficActuationControl trafficActuationControl;
  
  private JsonMapDataGenerator jsonMapDataGenerator;
  
  private StatisticsControl statisticsControl;

  private int simulationTime; 
  
  private Situation configuration;

  /**
   * @param traciService traci service
   * @param sumoStepLength simulation step length
   * @param maxVeh limit for vehicle count
   * @param situation infrastructure for simulation
   * @throws NoIntersectionInSituationException when there are no intersections present in infrastructure
   * @throws NoValidRoutesInSituationException when there are no valid routes present in infrastructure
   * @throws TraciException when some of the traci calls fails
   */
  public SimulationControlImpl(TraciService traciService, double sumoStepLength, int maxVeh, Situation situation)
          throws NoIntersectionInSituationException, NoValidRoutesInSituationException, TraciException
  {    
    this.configuration = situation;
    this.traciService = traciService;
    
    initSimulation();
    
    this.vehicleGenerationControl = new VehicleGenerationControlImpl(traciService, maxVeh, situation);
    this.signalProgramControl = new SignalProgramControlImpl(traciService);
    this.trafficActuationControl = new TrafficActuationControlImpl(traciService);
    this.jsonMapDataGenerator = new JsonMapDataGeneratorImpl(traciService, situation, sumoStepLength);
    this.statisticsControl = new StatisticsControlImpl(sumoStepLength);
  }
 
  private void initSimulation()
      throws NoIntersectionInSituationException, NoValidRoutesInSituationException, TraciException
  {
    if (configuration.getIntersectionList().size() == 0)
    {
      throw new NoIntersectionInSituationException("There are no valid intersections in situation");
    }

    if (configuration.getRoutes().size() == 0)
    {
      throw new NoValidRoutesInSituationException("There are no valid routes in situation");
    }    
    
    // start TraCI
    traciService.startSimulation();
    logger.info("Simulation initialized.");
    logger.info(configuration.getGeneratedRoutes().size() + " routes are available.");

    // load routes and initialize the simulation
    traciService.nextStep();
  }
  
  @Override
  public JsonSituationLayout generateMapData(int visualizationMultiplier) throws TraciException
  {
    return jsonMapDataGenerator.generateMapData(visualizationMultiplier);
  }

  @Override
  public void setVehicleGenerationRate(int genMod)
  {   
    vehicleGenerationControl.setVehicleGenerationRate(genMod); 
  }

  @Override
  public void dispatchEmergencyVehicle(int simulationTime) throws TraciException
  {
    vehicleGenerationControl.dispatchEmergencyVehicle(simulationTime);    
  }

  @Override
  public void setSignalProgramToIntersection(int intersectionId, String signalProgramId) throws TraciException
  {
    signalProgramControl.setSignalProgramToIntersection(intersectionId, signalProgramId);    
  }

  @Override
  public JsonStatistics getnerateStatistics()
  {
    return statisticsControl.generateStatistics();
  }

  @Override
  public void stopSimulation()
  {
    logger.info("Simulation finished");
    // stop TraCI
    
    if(traciService != null)
    {
      traciService.stopSimulation();
    }
  }

  @Override
  public List<JsonSimulationStep> performStepSequence(int numberOfSteps, int visualizationMultiplier) 
      throws TraciException, InfrastructureElementNotFoundException
  {
    List<JsonSimulationStep> list = new ArrayList<>();
    
    for(int i = 0; i < numberOfSteps; i++)
    {
        list.add(performStep(visualizationMultiplier));
    }
    
    return list;
  }

  private JsonSimulationStep performStep(int visualizationMultiplier) 
      throws TraciException, InfrastructureElementNotFoundException
  {        
      simulationTime = traciService.getSimulationTime();
      
      statisticsControl.setSimulationTime(simulationTime / 1000.0);
                
      trafficActuationControl.doTrafficActuation(configuration.getIntersectionList());
            
      List<JsonVehicle> vehicles = vehicleGenerationControl.generateVehicles(simulationTime, statisticsControl, visualizationMultiplier);
            
      traciService.nextStep();
             
      JsonSimulationStep result = getSimStepJsonResult(vehicles);
                  
      return result;      
  }

  /**
   * Gather the results of the simulation step
   * @param vehiclesToAdd vehicles to be added in simulation step
   * @return json object of data for vizualization
   * @throws TraciException when the traci call fails
   * @throws InfrastructureElementNotFoundException when some of required infrastracure is missing
   */
  private JsonSimulationStep getSimStepJsonResult(List<JsonVehicle> vehiclesToAdd) 
      throws TraciException, InfrastructureElementNotFoundException
  {
    
    List<JsonVehicleState> vehList = getVehicles();
  
    List<JsonTrafficLightState> tlsList = new ArrayList<>();
    List<JsonPhaseState> stateList = new ArrayList<>();
    List<String> intersectionIds = traciService.getIntersectionIds();
    for (String intersectionId : intersectionIds)
    {
      try 
      {
        Intersection intersection = configuration.getIntersectionById(Integer.parseInt(intersectionId)); 
        int phaseId = traciService.getTlsPhaseId(intersection.getId());
        String signalProgramId = traciService.getTlsSignalProgramId(intersection.getId());
        
        int nextSwitch = traciService.getNextSwitch(intersectionId);
              
        JsonPhaseState state = new JsonPhaseState(signalProgramId, intersection.getId(), phaseId, 
          nextSwitch / 1000.0);
        stateList.add(state);   
        
        tlsList.addAll(getTlsStates(intersection));
      }
      catch (NumberFormatException e)
      {
        throw new InfrastructureElementNotFoundException("Intersection by ID: " + intersectionId);
      }
      
      
    }
    
    return new JsonSimulationStep(simulationTime, vehList, tlsList, stateList, vehiclesToAdd);
  }

  private List<JsonTrafficLightState> getTlsStates(Intersection intersection) throws TraciException
  {
    List<JsonTrafficLightState> intersectionList = new ArrayList<>();
    
    String stateString = traciService.getTLStateString(String.valueOf(intersection.getId()));
    
    int stateStringIndex = 0;
    for (Leg leg : intersection.getLegs())
    {
      for (SignalGroup signalGroup : leg.getSignalGroups())
      {       
        for (Lane lane : signalGroup.getLanes())
        {
          intersectionList.add(new JsonTrafficLightState(lane.getLaneNetId(), stateString.charAt(stateStringIndex)));          
        }
        
        stateStringIndex +=  signalGroup.getDirections().getConnectionsCount() > intersection.getLegs().size()
          ? intersection.getLegs().size() - 1
          : signalGroup.getDirections().getConnectionsCount();        
      }
    }
    
    return intersectionList;
  }

  private List<JsonVehicleState> getVehicles() throws TraciException
  {
    List<JsonVehicleState> vehicleList = new ArrayList<>();
    List<String> vehicleIds = traciService.getVehicleIds();
    
    for (String vehicleId : vehicleIds)
    {
      vehicleList.add(traciService.getVehicleState(vehicleId));
      statisticsControl.updateVehicle(vehicleId, traciService.getVehicleStatistics(vehicleId));
    }
    
    return vehicleList;
  }
}
