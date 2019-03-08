package trafficsimulator.server.communication.enums;

/**
 * @author z003ru0y
 * Message states for communication with client
 */
public enum MessageState
{
  /***/
  OK(0), 
  /***/
  ERROR(1);

  int numValue;

  private MessageState(int numValue)
  {
    this.numValue = numValue;
  }

  /**
   * @return numeric value of enum
   */
  public int getNumValue()
  {
    return this.numValue;
  }
}