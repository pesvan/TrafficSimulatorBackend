package trafficsimulator.shared.exceptions;

/**
 * @author z003ru0y
 * When invalid direction is used
 */
public class UnknownDirection extends Exception
{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * @param message error message
   */
  public UnknownDirection(String message)
  {
    super(message);
  }

}
