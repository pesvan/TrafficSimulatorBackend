package trafficsimulator.generator.exceptions;

/**
 * @author z003ru0y
 * When sumo config files generation fails
 */
public class SumoNetworkGenerationException extends Exception
{

  private static final long serialVersionUID = 1L;

  /**
   * @param message error message
   */
  public SumoNetworkGenerationException(String message)
  {
    super(message);
  }
}
