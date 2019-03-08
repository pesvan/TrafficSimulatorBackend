package trafficsimulator.simulator.traci.dto;

import de.tudresden.ws.container.SumoColor;
import lombok.Data;
import trafficsimulator.shared.dto.VehicleType;

/**
 * Object which contains information about new vehicle which is to be added to simulation
 * @author Petr Svana
 *
 */
@Data
public class SumoVehicle
{
  private final SumoColor BLUE = new SumoColor(0, 0, 255, 100);
  private final SumoColor GREEN = new SumoColor(0, 255, 0, 100);
  private final SumoColor RED = new SumoColor(255, 0, 0, 100);
  private final SumoColor YELLOW = new SumoColor(255, 255, 0, 100);
  private final SumoColor PURPLE = new SumoColor(127, 0, 225, 100);

  private final String id;
  private final String vehicleClass;
  private final String shapeClass;
  private final int simtime;
  private final String route;
  private final SumoColor color;
  private final VehicleType vehicleType;
  
  /**
   * @param route route of the vehicle
   * @param id id of the vehicle
   * @param simtime simulation time when will be the vehicle added to simulation
   * @param vehicleType type of the vehicle
   */
  public SumoVehicle(String route, String id, int simtime, VehicleType vehicleType)
  {
    this.id = id;
    this.route = route;
    this.simtime = simtime;
    this.vehicleType = vehicleType;

    switch (vehicleType.getName())
    {
      case "van":
        this.vehicleClass = "passenger";
        this.shapeClass = "passenger/van";
        this.color = YELLOW;
        break;
      case "emergency":
        this.vehicleClass = "emergency";
        this.shapeClass = "passenger/van";
        this.color = YELLOW;
        break;
      case "bus_public":
        this.vehicleClass = "bus";
        this.shapeClass = "bus/city";
        this.color = PURPLE;
        break;
      case "bus_private":
        this.vehicleClass = "bus";
        this.shapeClass = "bus/overland";
        this.color = RED;
        break;
      case "truck":
        this.vehicleClass = "truck";
        this.shapeClass = "truck/trailer";
        this.color = GREEN;
        break;
      default:
        this.vehicleClass = "passenger";
        this.shapeClass = "passenger";
        this.color = BLUE;
        break;
    }
  }
}
