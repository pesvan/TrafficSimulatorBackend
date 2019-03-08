package trafficsimulator.shared.exceptions;

/**
 * @author z003ru0y
 *Raised when some element of the infrastructure is not found
 *Intersection, Leg, Lane...
 */
public class InfrastructureElementNotFoundException extends Exception
{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * @param message error message
   */
  public InfrastructureElementNotFoundException(String message)
  {
    super(message);
  }

}
