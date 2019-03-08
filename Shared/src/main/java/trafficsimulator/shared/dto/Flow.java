package trafficsimulator.shared.dto;

import lombok.Getter;

/**
 * Represents route from one leg to other
 * @author z003ru0y
 *
 */
public class Flow
{
  
  @Getter
  private final String id;
  
  @Getter
  private final Leg from;
  
  @Getter
  private final Leg to;

  /**
   * @param from leg
   * @param to leg
   */
  public Flow(Leg from, Leg to)
  {
    this.id = "route-" + from.getId()
      + "-"
      + to.getId();
    this.from = from;
    this.to = to;
  }

  @Override
  public String toString()
  {
    return getFrom().getEdgeInConnectionName() + " -> "
      + getTo().getEdgeOutConnectionName()
      + "\n";
  }
}
