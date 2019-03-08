package trafficsimulator.simulator.interfaces.impl;

import java.util.ArrayList;
import java.util.List;

import trafficsimulator.shared.dto.Intersection;
import trafficsimulator.shared.dto.IntersectionConnection;
import trafficsimulator.shared.dto.Lane;
import trafficsimulator.shared.dto.Leg;
import trafficsimulator.shared.dto.Situation;
import trafficsimulator.simulator.exceptions.TraciException;
import trafficsimulator.simulator.interfaces.JsonMapDataGenerator;
import trafficsimulator.simulator.output.dto.JsonConnectionLeg;
import trafficsimulator.simulator.output.dto.JsonConnectionPolygon;
import trafficsimulator.simulator.output.dto.JsonCoordinates;
import trafficsimulator.simulator.output.dto.JsonDirections;
import trafficsimulator.simulator.output.dto.JsonIntersection;
import trafficsimulator.simulator.output.dto.JsonIntersectionLeg;
import trafficsimulator.simulator.output.dto.JsonLane;
import trafficsimulator.simulator.output.dto.JsonLayoutMetadata;
import trafficsimulator.simulator.output.dto.JsonSituationLayout;
import trafficsimulator.simulator.output.dto.JsonVehicle;
import trafficsimulator.simulator.traci.TraciService;
import trafficsimulator.simulator.traci.dto.VehicleInfo;

/**
 * @author z003ru0y
 *
 */
public class JsonMapDataGeneratorImpl implements JsonMapDataGenerator
{  
  private final TraciService traciService;
  
  private final Situation configuration;
  
  private final double sumoSimStepLength;
  
  /**
   * @param traciService traci service
   * @param configuration infrastructure configuration
   * @param stepLength simulation step length
   */
  public JsonMapDataGeneratorImpl(TraciService traciService, Situation configuration, double stepLength)
  {
    this.traciService = traciService;
    this.configuration = configuration;
    this.sumoSimStepLength = stepLength;
  }

  @Override
  public JsonSituationLayout generateMapData(int visualizationMultiplier) throws TraciException
  {    
    JsonLayoutMetadata metadata = generateMetadata();

    List<JsonIntersection> jsonIntersectionList = generateIntersectionList();

    List<JsonConnectionLeg> jsonConnectionList = generateConnectionList();
    
    List<JsonConnectionPolygon> jsonConnectionPolygonList = generateConnectionPolygons();
    
    List<VehicleInfo> alreadyExistingVehicles = traciService.getAlreadyExistingVehicles();
    
    List<JsonVehicle> jsonAlreadyExistingVehicles = convertVehicleInfosToJsonVehicles(alreadyExistingVehicles, visualizationMultiplier);    

    return new JsonSituationLayout(metadata, jsonIntersectionList, jsonConnectionList, jsonConnectionPolygonList, jsonAlreadyExistingVehicles);
  }
  
  private List<JsonVehicle> convertVehicleInfosToJsonVehicles(
      List<VehicleInfo> vehicles, int visualizationMultiplier)
  {
    List<JsonVehicle> multipliedVehicles = new ArrayList<>();
    for (VehicleInfo vehicle : vehicles)
    {
      multipliedVehicles.add(
          new JsonVehicle(
              vehicle.getId(), 
              vehicle.getVehLength() * visualizationMultiplier, 
              vehicle.getVehWidth() * visualizationMultiplier, 
              vehicle.getHexColor()
              ));
    }
    
    return multipliedVehicles;
  }

  private List<JsonConnectionPolygon> generateConnectionPolygons() throws TraciException
  {
    List<JsonConnectionPolygon> jsonConnectionPolygonList = new ArrayList<>();
    
    for (IntersectionConnection connection : configuration.getIntersectionConnections())
    {
      jsonConnectionPolygonList.add(new JsonConnectionPolygon(traciService.getIntersectionShape(connection.getLegI1().getId())));
      jsonConnectionPolygonList.add(new JsonConnectionPolygon(traciService.getIntersectionShape(connection.getLegI2().getId())));
    }
    
    return jsonConnectionPolygonList;
  }

  private JsonLayoutMetadata generateMetadata() throws TraciException
  {
    int intersectionCount = configuration.getIntersectionList().size();
    int routesCount = configuration.getGeneratedRoutes() == null ? 0 : configuration.getGeneratedRoutes().size();
    if (intersectionCount==0)
    {
      routesCount = 0;
    }
    
    JsonCoordinates boundary = traciService.getNetworkBoundary();
    return new JsonLayoutMetadata(sumoSimStepLength, intersectionCount, routesCount, boundary);
  }
  
  private List<JsonIntersection> generateIntersectionList() throws TraciException
  {
    List<JsonIntersection> jsonIntersectionList = new ArrayList<>();
    for (Intersection intersection : configuration.getIntersectionList())
    {
      List<JsonIntersectionLeg> legList = new ArrayList<>();
      for (Leg leg : intersection.getLegs())
      {
        List<JsonLane> laneList = new ArrayList<>();
        for (Lane lane : leg.getAllLanes())
        {
          laneList.add(
            new JsonLane(lane.getLaneNetId(),
              true,
              generateLaneDirections(lane),
              traciService.getLaneLength(lane.getLaneNetId()),
              traciService.getLaneShape(lane.getLaneNetId())));
        }
        
        //TODO: do it more general
        String outputLaneId = leg.getEdgeOutConnectionName()+"_0";
        laneList.add(new JsonLane(outputLaneId, 
          false,
          null, 
          traciService.getLaneLength(outputLaneId),
          traciService.getLaneShape(outputLaneId)));
        
        legList.add(new JsonIntersectionLeg(
          leg.getId(), leg.getAngle().getValue(), laneList));
      }
      jsonIntersectionList.add(new JsonIntersection(
        intersection.getId() + "",
        traciService.getIntersectionShape(intersection.getId()+""),
        intersection.getGridPosition(),
        intersection.getAngle().getValue(), 
        legList,
        intersection.getSignalProgramIds()));

    }
    return jsonIntersectionList;
  }

  private List<JsonConnectionLeg> generateConnectionList() throws TraciException
  {
    List<JsonConnectionLeg> jsonConnectionList = new ArrayList<>();
    for (IntersectionConnection connection : configuration.getIntersectionConnections())
    {
      jsonConnectionList.add(
        new JsonConnectionLeg(
          connection.getId(), connection.getLegI1().getId(), connection.getLegI2().getId(), traciService.getLaneShape(connection.getId()+"_0")));
      jsonConnectionList.add(
        new JsonConnectionLeg(
          connection.getIdBack(), connection.getLegI2().getId(), connection.getLegI1().getId(), traciService.getLaneShape(connection.getIdBack()+"_0")));

    }
    return jsonConnectionList;
  }
  
  private JsonDirections generateLaneDirections(Lane lane)
  {
    return new JsonDirections(lane.getDirections().isLeft(), lane.getDirections().isRight(), lane.getDirections().isStraight());
  }

}
