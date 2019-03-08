package trafficsimulator.parser.exceptions;

/**
 * @author z003ru0y
 * When there is invalid value in the XML configuration
 */
public class XMLElementInvalidValueException extends Exception
{

  private static final long serialVersionUID = 1L;

  /**
   * @param message error message
   */
  public XMLElementInvalidValueException(String message)
  {
    super(message);
  }
}
