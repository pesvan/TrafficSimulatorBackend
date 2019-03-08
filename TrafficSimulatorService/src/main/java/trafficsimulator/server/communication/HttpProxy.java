package trafficsimulator.server.communication;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import trafficsimulator.generator.SumoNetworkConfigurationGenerator;
import trafficsimulator.generator.exceptions.SumoNetworkGenerationException;
import trafficsimulator.parser.ConfigurationParser;
import trafficsimulator.parser.exceptions.NoXmlStringProvidedException;
import trafficsimulator.parser.exceptions.XMLElementInvalidValueException;
import trafficsimulator.parser.exceptions.XMLElementNotFoundException;
import trafficsimulator.server.communication.dto.JsonMessage;
import trafficsimulator.server.communication.enums.LaneOperation;
import trafficsimulator.server.communication.enums.MessageState;
import trafficsimulator.server.session.SessionMemory;
import trafficsimulator.shared.dto.Directions;
import trafficsimulator.shared.dto.GridPosition;
import trafficsimulator.shared.dto.Intersection;
import trafficsimulator.shared.dto.Lane;
import trafficsimulator.shared.dto.Leg;
import trafficsimulator.shared.dto.SignalGroup;
import trafficsimulator.shared.enumerators.VehicleTypeEnum;
import trafficsimulator.shared.exceptions.InfrastructureElementNotFoundException;
import trafficsimulator.shared.exceptions.LaneOperationException;
import trafficsimulator.shared.exceptions.LegValidationException;
import trafficsimulator.shared.exceptions.NoVehicleTypesForSimulation;
import trafficsimulator.shared.exceptions.OccupiedGridPositionException;
import trafficsimulator.shared.exceptions.UnknownDirection;
import trafficsimulator.shared.helper.Computations;
import trafficsimulator.simulator.exceptions.NoIntersectionInSituationException;
import trafficsimulator.simulator.exceptions.NoValidRoutesInSituationException;
import trafficsimulator.simulator.exceptions.TraciException;
import trafficsimulator.simulator.output.dto.JsonCoordinates;
import trafficsimulator.simulator.output.dto.JsonLayoutMetadata;
import trafficsimulator.simulator.output.dto.JsonRecievedConf;
import trafficsimulator.simulator.output.dto.JsonSimulationStep;
import trafficsimulator.simulator.output.dto.JsonSituationLayout;

/**
 * @author z003ru0y
 * Class which handled requests from the javascript client
 */
@RestController
@CrossOrigin
@Component
public class HttpProxy
{
  private static Logger logger = LoggerFactory.getLogger(HttpProxy.class);
  
  @Autowired
  private ConfigurationParser parser;
  
  @Autowired
  private SumoNetworkConfigurationGenerator generator;
  
  @Autowired
  private SessionMemory session;
  
  @Autowired
  private Computations computations;
   

  /**
   * Retrieves current infrastructure layout
   * @return json object with infrastructure layout
   * @throws TraciException when some of the traci calls fails
   * @throws NoIntersectionInSituationException when there are no intersections present in infrastructure
   * @throws NoValidRoutesInSituationException when there are no valid routes present in infrastructure
   */
  @GetMapping(value = "/getMap")
  public JsonMessage getSituationLayout()
      throws TraciException, NoIntersectionInSituationException, NoValidRoutesInSituationException
  {
    logger.debug("Got /getMap request from client");
    
    if (session.getSituation().getIntersectionList().size() > 0)
    {
      JsonSituationLayout layout = session.getSimulationControl(true).generateMapData(session.getVisualizationMultiplier());
      session.deleteCurrentRunner();
      return new JsonMessage(layout);
    }
    else
    {
      JsonLayoutMetadata metadata = new JsonLayoutMetadata(0, 0, 0, new JsonCoordinates(0.0, 0.0));
      JsonSituationLayout layout = new JsonSituationLayout(
        metadata, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
      return new JsonMessage(layout);
    }      
  }
  
  /**
   * Retrieves statistical data about current simulation
   * @return json object with statistical data
   * @throws TraciException when some of the traci calls fails
   * @throws NoIntersectionInSituationException when there are no intersections present in infrastructure
   * @throws NoValidRoutesInSituationException when there are no valid routes present in infrastructure
   */
  @GetMapping(value = "/getStatistics")
  public JsonMessage getStatistics()
      throws NoIntersectionInSituationException, NoValidRoutesInSituationException, TraciException
  {
    return new JsonMessage(session.getSimulationControl(false).getnerateStatistics());
  }
  
  /**
   * Sets the density of traffic
   * @param density traffic generation rate
   * @return generic json message object
   * @throws TraciException when some of the traci calls fails
   * @throws NoIntersectionInSituationException when there are no intersections present in infrastructure
   * @throws NoValidRoutesInSituationException when there are no valid routes present in infrastructure
   */
  @GetMapping(value = "/setTrafficDensity")
  public JsonMessage setTrafficDensity(@RequestParam int density)
      throws NoIntersectionInSituationException, NoValidRoutesInSituationException, TraciException
  {
    logger.debug("Got /setTrafficDensity request from client with param {}", density);
        
    session.getSimulationControl(false).setVehicleGenerationRate(density);
    
    return new JsonMessage();
  }
  
  /**
   * Sets another signal program to specific intersection
   * @param intersectionNo id of an intersection
   * @param programId id of a signal program
   * @return generic json message object
   * @throws TraciException when some of the traci calls fails
   * @throws NoIntersectionInSituationException when there are no intersections present in infrastructure
   * @throws NoValidRoutesInSituationException when there are no valid routes present in infrastructure
   */
  @GetMapping(value = "/setSignalProgram")
  public JsonMessage setSignalProgram(@RequestParam int intersectionNo, @RequestParam String programId)
      throws TraciException, NoIntersectionInSituationException, NoValidRoutesInSituationException
  {
    logger.debug("Got /setSignalProgram request from client with params {} {}", intersectionNo, programId);
     
    session.getSimulationControl(false).setSignalProgramToIntersection(intersectionNo, programId);
    
    return new JsonMessage();
  }
  
  
  /**
   * Removes intersection from infrastructure layout
   * @param intersectionId id of the intersection to delete
   * @return generic json message object
   * @throws UnknownDirection when some invalid direction occurs
   * @throws SumoNetworkGenerationException when generation of configuration files fails
   * @throws InfrastructureElementNotFoundException when some element of infrastructure is missing
   */
  @GetMapping(value = "/deleteIntersection")
  public JsonMessage deleteIntersection(@RequestParam int intersectionId)
      throws UnknownDirection, SumoNetworkGenerationException, InfrastructureElementNotFoundException
  {
    logger.debug("Got /deleteIntersection request from client with param {}", intersectionId);    

    session.getSituation().deleteIntersection(intersectionId);
    computations.connectIntersections(session.getSituation());
    generator.generateConfiguration(session.getSituation());
    
    return new JsonMessage();
  }
  
  /**
   * Handles lane operations: add, delete, change
   * TODO: split
   * @param legId id of a leg where we want to put the lane
   * @param intersectionId id of a intersection where we want to put the lane
   * @param left direction
   * @param straight direction
   * @param right direction
   * @param laneId id of a lane to be changed or deleted
   * @param operation lane operation
   * @return generic json message object
   * @throws SumoNetworkGenerationException when generation of configuration files fails
   * @throws LaneOperationException when lane operation itself encounters error
   * @throws InfrastructureElementNotFoundException when some element of infrastructure is missing
   */
  @GetMapping(value = "/laneOperation")
  public JsonMessage laneOperation(@RequestParam String legId, @RequestParam int intersectionId,
    @RequestParam(required = false) Boolean left, 
    @RequestParam(required = false) Boolean straight, 
    @RequestParam(required = false) Boolean right,
    @RequestParam String laneId,
    @RequestParam String operation) 
        throws SumoNetworkGenerationException, LaneOperationException, InfrastructureElementNotFoundException
  {
    logger.debug("Got /laneOperation request from client with param {} {} {} {} {} {} {}", 
      intersectionId, legId, laneId, left, straight, right, operation);
    
    LaneOperation laneOperation = LaneOperation.fromKey(operation).get();
    
    Intersection intersection = session.getSituation().getIntersectionById(intersectionId);    
    Leg leg = intersection.getLegById(legId);
    
    SignalGroup laneSg = null;
    Lane originalLane = null;
    
    for (SignalGroup sg : leg.getSignalGroups())
    {
      for (Lane ln : sg.getLanes())
      {
        if(ln.getLaneNetId().equals(laneId))
        {
          laneSg = sg;
          originalLane = ln;
        }
      }
    }
    
    if (originalLane == null || laneSg == null)
    {
      throw new LaneOperationException("Lane was not found: " + laneId);        
    }
    
    Directions directions;
    
    switch (laneOperation)
    {
      case ADD:
        logger.debug("Performing ADD operation");
        directions = new Directions(straight, left, right, false);
        if(directions.hasNoDirection())
        {
          throw new LaneOperationException("Lane was not added, resulting lane has to have at least one direction: " + laneId);
        }
        int newLaneId = laneSg.getNextLaneId();        
        Lane lane = new Lane(newLaneId, directions, leg.getEdgeInConnectionName() + "_" + newLaneId);
        laneSg.addLane(lane);
        
        try 
        {
          leg.validateLeg();
        }
        catch (LegValidationException e) {          
          laneSg.deleteLane(lane);
          throw new LaneOperationException("Lane was not added, Leg validation failed: " + e.getMessage());          
        }
        
        break;
      case CHANGE:
        logger.debug("Performing CHANGE operation");
        directions = new Directions(straight, left, right, false);
        if(directions.hasNoDirection())
        {
          throw new LaneOperationException("Lane was not changed, resulting lane has to have at least one direction: " + laneId);
        }
        Directions originalDirections = originalLane.getDirections();
        laneSg.changeLane(originalLane, directions, leg);
        
        try 
        {
          leg.validateLeg();
        }
        catch (LegValidationException e) {          
          laneSg.changeLane(originalLane, originalDirections, leg);
          throw new LaneOperationException("Lane was not changed, Leg validation failed: " + e.getMessage()); 
        }        
    
        break;
      case DELETE:
        logger.debug("Performing DELETE operation");

        if (laneSg.getLanes().size() < 2)
        {
          throw new LaneOperationException("Lane was not deleted, since it is the last one: " + laneId); 
        }
        else
        {
          laneSg.deleteLane(originalLane);
        }
        break;
      default:
        throw new LaneOperationException("Unknown operation: " + operation); 
    }
    
    leg.calculateTurns(intersection.getLegs());
    leg.getAllLanes().forEach(lane -> lane.calculateOutputLegs(leg));
    

    generator.generateConfiguration(session.getSituation());


        
    return new JsonMessage();
  }
  
  /**
   * Performs multiple steps in a simulation
   * @param noOfSteps number of steps to perform at once
   * @return json object with data about performed steps
   * @throws TraciException when some of the traci calls fails
   * @throws NoIntersectionInSituationException when there are no intersections present in infrastructure
   * @throws NoValidRoutesInSituationException when there are no valid routes present in infrastructure
   * @throws InfrastructureElementNotFoundException when some element of infrastructure is missing
   */
  @GetMapping(value = "/getDataMultipleStep")
  public JsonMessage getSimulationDataMultipleStep(@RequestParam int noOfSteps)
      throws TraciException, NoIntersectionInSituationException, 
      NoValidRoutesInSituationException, InfrastructureElementNotFoundException 
  {
    logger.debug("Got /getDataMultipleStep request from client with param {}", noOfSteps);     
  
    List<JsonSimulationStep> list = session.getSimulationControl(true).performStepSequence(noOfSteps, session.getVisualizationMultiplier());
    return new JsonMessage(list);
  }
  
  /**
   * Resets everything in a simulation
   * @return generic json message
   */
  @GetMapping(value = "/resetSimulation")
  public JsonMessage clearSituation()
  {
    logger.info("Got /resetSimulation request from client");
    
    session.setUpNewSessionData();
    session.deleteCurrentRunner();
    
    return new JsonMessage();
  }
  
  /**
   * Correctly stops simulation
   * @return generic json message
   * @throws NoIntersectionInSituationException when there are no intersections present in infrastructure
   * @throws NoValidRoutesInSituationException when there are no valid routes present in infrastructure
   * @throws TraciException when some of the traci calls fails
   */
  @GetMapping(value = "/stopSimulation")
  public JsonMessage stopSimulation()
      throws NoIntersectionInSituationException, NoValidRoutesInSituationException, TraciException
  {
    logger.info("Got /stopSimulation request from client");
    
    if (session.hasSimulationControl())
    {
      session.getSimulationControl(false).stopSimulation();
      session.deleteCurrentRunner();
    }    
        
    return new JsonMessage();
  }
    
  /**
   * Adds new intersection to traffic infrastructure
   * @param request json object with the request
   * @return generic json message
   * @throws NoVehicleTypesForSimulation when no vehycle types were selected
   * @throws UnknownDirection when invalid direction was selected
   * @throws OccupiedGridPositionException when the position to put intersection is occupied
   * @throws SumoNetworkGenerationException when the configuration files generation fails
   * @throws NoXmlStringProvidedException when the configuration was empty
   * @throws XMLElementNotFoundException when some of the essential xml elements was not found in configuration
   * @throws XMLElementInvalidValueException when some of the xml elements has invalid value
   * @throws InfrastructureElementNotFoundException when some of the infrastructure elements is missing
   */
  @PostMapping(value = "/sendConfiguration")
  public JsonMessage addIntersection(@RequestBody JsonRecievedConf request) 
      throws NoVehicleTypesForSimulation, UnknownDirection, OccupiedGridPositionException, 
      SumoNetworkGenerationException, NoXmlStringProvidedException, 
      XMLElementNotFoundException, XMLElementInvalidValueException, InfrastructureElementNotFoundException
  {
    logger.info("Got /sendConfiguration request from client");
  
    setVehicleTypes(request);
    
    Optional<GridPosition> gridPosition = resolveGridPosition(request);
    Intersection parsedIntersection = null;
    /**
     * parsing is disabled in public version
     *
     */
    session.getSituation().addIntersection(parsedIntersection);
    computations.connectIntersections(session.getSituation());
    
    generator.generateConfiguration(session.getSituation()); 
    
    return new JsonMessage();
  }
  
  private Optional<GridPosition> resolveGridPosition(JsonRecievedConf request) 
    throws UnknownDirection, OccupiedGridPositionException, InfrastructureElementNotFoundException
  {
    Optional<GridPosition> gridPosition = Optional.empty();
    if (request.isFirstIntersection())
    {
      gridPosition = Optional.ofNullable(new GridPosition(0, 0));
    }
    else
    {
      Intersection relativeToIntersection = session.getSituation().getIntersectionById(request.getSelectedIntersectionId());
      gridPosition = Optional.ofNullable(computations.getRelativeGridPosition(
        relativeToIntersection.getGridPosition(),
        request.getPosition()));
      computations.assertGridPositionIsFree(gridPosition.get(), session.getSituation().getIntersectionList());
    }
    
    return gridPosition;
  }
  
  private void setVehicleTypes(JsonRecievedConf request) throws NoVehicleTypesForSimulation
  {
    if (request.isFirstIntersection())
    {
      if(request.isCarsAllowed()) 
      {
    	session.getSituation().setVehicleType(VehicleTypeEnum.CAR);
      }
      if(request.isVanAllowed()) 
      {
        session.getSituation().setVehicleType(VehicleTypeEnum.VAN);
      }
      if(request.isBusPublicAllowed()) 
      {
    	session.getSituation().setVehicleType(VehicleTypeEnum.BUS_PUBLIC);
      }
      if(request.isBusPrivateAllowed()) 
      {
    	session.getSituation().setVehicleType(VehicleTypeEnum.BUS_PRIVATE);
      }
      if(request.isTrucksAllowed()) 
      {
    	session.getSituation().setVehicleType(VehicleTypeEnum.TRUCK);
      }
      
      if (session.getSituation().getVehicleTypes().size() == 0)
      {
        throw new NoVehicleTypesForSimulation("");
      }
    }
  }
  
  /**
   * Handles exception from all the requests handlers
   * @param exception to be processed
   * @return general json message with error
   */
  @ExceptionHandler({Exception.class})
  public JsonMessage handleException(Exception exception)
  {
    String errorMessage;
    if (exception instanceof NoVehicleTypesForSimulation)
    {
      errorMessage = "No vehicle types are set for the simulation";
    }
    else if (exception instanceof OccupiedGridPositionException)
    {
      errorMessage = "Trying to add intersection at occupied grid position: " + exception.getMessage();
    }
    else if (exception instanceof UnknownDirection)
    {
      errorMessage = "Internal error: unknown direction";
    }
    else if (exception instanceof NoXmlStringProvidedException)
    {
      errorMessage = "No configuration provided";
    }
    else if (exception instanceof SumoNetworkGenerationException)
    {
      errorMessage = "Configuration generation error: " + exception.getMessage();
    }
    else if (exception instanceof XMLElementNotFoundException)
    {
      errorMessage = "Configuration parsing error: XML element not found: " + exception.getMessage();
    }
    else if (exception instanceof XMLElementInvalidValueException)
    {
      errorMessage = "Configuration parsing error: XML element has invalid value: " + exception.getMessage();
    }
    else if (exception instanceof NoIntersectionInSituationException)
    {
      errorMessage = "No intersection in the situation is present";
    }
    else if (exception instanceof NoValidRoutesInSituationException)
    {
      errorMessage = "No valid route in the situation is present";
    }
    else if (exception instanceof TraciException)
    {
      errorMessage = "Internal simulation error: " + exception.getMessage();
    }
    else if (exception instanceof LaneOperationException)
    {
      errorMessage = "Internal simulation error: " + exception.getMessage();
    }
    else if (exception instanceof InfrastructureElementNotFoundException)
    {
      errorMessage = "Situation infrastructure element not found: " + exception.getMessage();
    }
    else
    {
      errorMessage = "general error";
      exception.printStackTrace();
    }
    
    
    logger.error(errorMessage);
    return new JsonMessage(MessageState.ERROR, errorMessage);
  }

}
