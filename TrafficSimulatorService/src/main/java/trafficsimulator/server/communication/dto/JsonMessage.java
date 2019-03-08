package trafficsimulator.server.communication.dto;

import lombok.Data;
import trafficsimulator.server.communication.enums.MessageState;

/**
 * @author z003ru0y
 * Generic response to the client
 */
@Data
public class JsonMessage
{
  private final int status;
  private final String statusMessage;
  private final Object payload;
  
  /**
   * "ok" message without data
   */
  public JsonMessage()
  {
    this.status = MessageState.OK.getNumValue();
    this.statusMessage = "";
    this.payload = null;
  }
  
  /**
   * Genral "ok" message with some data
   * @param payload data to send to client
   */
  public JsonMessage(Object payload)
  {
    this.status = MessageState.OK.getNumValue();
    this.statusMessage = "";
    this.payload = payload;
  }
  
  /**
   * Error message without data
   * @param status status (0 or 1)
   * @param statusMessage error message
   */
  public JsonMessage(MessageState status, String statusMessage)
  {
    this.status = status.getNumValue();
    this.statusMessage = statusMessage;
    this.payload = null;
  }
  
}
