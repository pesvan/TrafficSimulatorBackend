package trafficsimulator.simulator.exceptions;

/**
 * @author z003ru0y
 * When there are no valid routes to simulate traffic on
 */
public class NoValidRoutesInSituationException extends Exception
{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * @param message error message
   */
  public NoValidRoutesInSituationException(String message)
  {
    super(message);
  }

}
