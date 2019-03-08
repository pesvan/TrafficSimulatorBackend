package trafficsimulator.shared.enumerators;

/**
 * @author z003ru0y
 * vehicle types for SUMO
 */
public enum VehicleTypeEnum
{
  /***/
  CAR("car"),
  /***/
  VAN("van"),
  /***/
  BUS_PUBLIC("bus_public"),
  /***/
  BUS_PRIVATE("bus_private"),
  /***/
  TRUCK("truck"),
  /***/
  EMERGENCY("emergency");
  
  private final String key;
  
  private VehicleTypeEnum(String key)
  {
    this.key = key;
  }
  
  /**
   * @return enum value string
   */
  public String getValue()
  {
    return key;
  }  
  
}
