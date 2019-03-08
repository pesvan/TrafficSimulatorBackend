package trafficsimulator.simulator.interfaces.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tudresden.ws.container.SumoColor;
import trafficsimulator.shared.dto.GeneratedRoute;
import trafficsimulator.shared.dto.Situation;
import trafficsimulator.shared.dto.VehicleType;
import trafficsimulator.shared.enumerators.VehicleTypeEnum;
import trafficsimulator.simulator.exceptions.TraciException;
import trafficsimulator.simulator.interfaces.StatisticsControl;
import trafficsimulator.simulator.interfaces.VehicleGenerationControl;
import trafficsimulator.simulator.output.dto.JsonVehicle;
import trafficsimulator.simulator.traci.TraciService;
import trafficsimulator.simulator.traci.dto.SumoVehicle;

/**
 * @author z003ru0y
 *
 */
public class VehicleGenerationControlImpl implements VehicleGenerationControl
{  
  private static Logger logger = LoggerFactory.getLogger(VehicleGenerationControlImpl.class);
  
  private final int maximumNumberOfVehicles;

  private final TraciService traciService;

  private final int CARS_BY_STEP = 1;

  private int generateVehicleEveryXStep = 15;
  
  private List<GeneratedRoute> generatedRoutes;
  private List<VehicleType> vehicleTypes;
  

  /**
   * @param traciService traci service
   * @param maxVeh limit of vehicle count
   * @param situation infrastructure for simulation
   */
  public VehicleGenerationControlImpl(TraciService traciService, int maxVeh, Situation situation)
  {
    this.traciService = traciService;
    maximumNumberOfVehicles = maxVeh;
    generatedRoutes = situation.getGeneratedRoutes();
    vehicleTypes = situation.getVehicleTypes();  
  }

  @Override
  public void setVehicleGenerationRate(int genMod)
  {
    this.generateVehicleEveryXStep = genMod;
  }

  @Override
  public void dispatchEmergencyVehicle(int simulationTime) throws TraciException
  {
    String vehId = simulationTime + "em";
    String route = this.pickRoute();
    SumoVehicle vehicle = new SumoVehicle(route, vehId, simulationTime + 1, new VehicleType(VehicleTypeEnum.EMERGENCY, 10.0, 10.0, 7.5));
    traciService.addVehicle(vehicle);
  }

  @Override
  public List<JsonVehicle> generateVehicles(int simulationTime, StatisticsControl statistics, int visualizationMultiplier) throws TraciException
  {
    List<JsonVehicle> newVehicles = new ArrayList<>();
    
    logger.debug("Currently we have {} vehicles running around", traciService.getVehicleCount());
    if (traciService.getVehicleCount() < maximumNumberOfVehicles && (simulationTime / 1000) % this.generateVehicleEveryXStep == 0)
    {
      for (int i = 0; i < this.CARS_BY_STEP; i++)
      {
        String route = this.pickRoute();
        VehicleType vType = pickVehicleType();
        String vehId = String.valueOf(vType.getName() + "_" + System.currentTimeMillis());
        SumoVehicle vehicle = new SumoVehicle(route, vehId, simulationTime + 1, vType);

        SumoColor color = vehicle.getColor();
        String hexColor = String.format("#%02X%02X%02X", color.r, color.g, color.b);

        // XXX hardcoded width
        newVehicles.add(new JsonVehicle(
            vehId, vType.getLength() * visualizationMultiplier, 1.8 * visualizationMultiplier, hexColor));

        traciService.addVehicle(vehicle);

        statistics.addVehicle(vehId);

        logger.debug("[{}] Vehicle {} ({})generated and put to {}", simulationTime, vehId, vType.getName(), route);
      }
    }

    return newVehicles;
  }

  private String pickRoute()
  {
    int randomNum = ThreadLocalRandom.current().nextInt(0, generatedRoutes.size());
    return generatedRoutes.get(randomNum).getEdges();
  }

  private VehicleType pickVehicleType()
  {
    int randomNum = ThreadLocalRandom.current().nextInt(0, vehicleTypes.size() * 3);

    if (randomNum >= vehicleTypes.size())
    {
      return vehicleTypes.get(0);
    }
    else
    {
      return vehicleTypes.get(randomNum);
    }

  }
  


}
