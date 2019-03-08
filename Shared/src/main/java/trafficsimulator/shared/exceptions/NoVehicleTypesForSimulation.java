package trafficsimulator.shared.exceptions;

/**
 * @author z003ru0y
 * When user does not select any vehicle types
 */
public class NoVehicleTypesForSimulation extends Exception
{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * @param message error message
   */
  public NoVehicleTypesForSimulation(String message)
  {
    super(message);
  }

}
