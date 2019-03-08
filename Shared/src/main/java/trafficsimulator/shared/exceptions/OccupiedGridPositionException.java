package trafficsimulator.shared.exceptions;

/**
 * @author z003ru0y
 * When the position to put intersection already contains a intersection
 */
public class OccupiedGridPositionException extends Exception
{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * @param message error message
   */
  public OccupiedGridPositionException(String message)
  {
    super(message);
  }

}
