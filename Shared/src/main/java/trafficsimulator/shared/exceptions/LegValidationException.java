package trafficsimulator.shared.exceptions;

/**
 * @author z003ru0y
 * When leg validation fails
 */
public class LegValidationException extends RuntimeException
{

  private static final long serialVersionUID = 1L;

  /**
   * @param message error message
   */
  public LegValidationException(String message)
  {
    super(message);
  }
}
