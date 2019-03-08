package trafficsimulator.parser.exceptions;

/**
 * @author z003ru0y
 * switch time not found in the internal representation 
 */
public class SwitchTimeNotFoundException extends Exception
{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * @param message error message
   */
  public SwitchTimeNotFoundException(String message)
  {
    super(message);
  }

}
