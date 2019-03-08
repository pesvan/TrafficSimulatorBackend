package trafficsimulator.shared.exceptions;

/**
 * @author z003ru0y
 * When lane operation fails
 */
public class LaneOperationException extends RuntimeException
{

  private static final long serialVersionUID = 1L;

  /**
   * @param message error message
   */
  public LaneOperationException(String message)
  {
    super(message);
  }
}
