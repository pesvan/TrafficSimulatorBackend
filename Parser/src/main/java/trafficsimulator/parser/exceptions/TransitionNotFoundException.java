package trafficsimulator.parser.exceptions;

/**
 * @author z003ru0y
 * transition between phases not found in the internal representation
 */
public class TransitionNotFoundException extends Exception
{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * @param message error message
   */
  public TransitionNotFoundException(String message)
  {
    super(message);
  }

}
