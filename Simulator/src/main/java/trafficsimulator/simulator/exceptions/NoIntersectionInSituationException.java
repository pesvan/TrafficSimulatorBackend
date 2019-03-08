package trafficsimulator.simulator.exceptions;

/**
 * @author z003ru0y
 * When there is no intersection to simulate traffic on
 */
public class NoIntersectionInSituationException extends Exception
{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * @param message error message
   */
  public NoIntersectionInSituationException(String message)
  {
    super(message);
  }

}
