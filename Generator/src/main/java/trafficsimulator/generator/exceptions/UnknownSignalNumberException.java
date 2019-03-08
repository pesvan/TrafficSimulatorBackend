package trafficsimulator.generator.exceptions;

/**
 * @author z003ru0y
 * When the Ocit number is not known
 */
public class UnknownSignalNumberException extends Exception
{

  private static final long serialVersionUID = 1L;

  /**
   * @param message error message
   */
  public UnknownSignalNumberException(String message)
  {
    super(message);
  }
}
