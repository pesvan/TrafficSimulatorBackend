package trafficsimulator.parser.exceptions;

/**
 * @author z003ru0y
 * When the XML element was not found in the configuration
 */
public class XMLElementNotFoundException extends Exception
{

  private static final long serialVersionUID = 1L;

  /**
   * @param message error message
   */
  public XMLElementNotFoundException(String message)
  {
    super(message);
  }
}
