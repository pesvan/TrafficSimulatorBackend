package trafficsimulator.server.communication.enums;

import java.util.Optional;

/**
 * @author z003ru0y
 * Client operations for lane
 */
public enum LaneOperation
{
  /***/
  ADD("add"), 
  /***/
  CHANGE("change"), 
  /***/
  DELETE("delete");

  private final String key;
  
  private LaneOperation(String key)
  {
    this.key = key;
  }
  
  
  /**
   * @param key string with lane operation
   * @return Optional of lane operation
   */
  public static final Optional<LaneOperation> fromKey(final String key)
  {
    for (final LaneOperation type : values())
    {
      if (type.key.equals(key))
      {
        return Optional.of(type);
      }
    }
    return Optional.empty();
  }
  
  /**
   * @return string value of the enum
   */
  public String getValue()
  {
    return key;
  }
}
