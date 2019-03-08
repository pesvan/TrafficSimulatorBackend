package trafficsimulator.shared.enumerators;

import java.util.Optional;

/**
 * @author z003ru0y
 *
 */
public enum Direction
{
  /***/
  BOTTOM("down"), 
  /***/
  LEFT("left"), 
  /***/
  TOP("up"), 
  /***/
  RIGHT("right");

  private final String key;
  
  private Direction(String key)
  {
    this.key = key;
  }
  
  
  /**
   * @param key to create Direction from
   * @return Optional of Direction
   */
  public static final Optional<Direction> fromKey(final String key)
  {
    for (final Direction type : values())
    {
      if (type.key.equals(key))
      {
        return Optional.of(type);
      }
    }
    return Optional.empty();
  }
  
  /**
   * @return string value of enum
   */
  public String getValue()
  {
    return key;
  }
}
