package trafficsimulator.shared.dto;

import java.util.List;

import lombok.Data;

/**
 * DTO transition
 * @author z003ru0y
 *
 */
@Data
public class Transition
{  
  private final int id;
  private final int duration;
  private final Phase fromPhase;
  private final Phase toPhase;
  private final List<SwitchingTime> switchingTimeList;
  
  @Override
  public String toString()
  {
    return "Transition " + id
      + " from Phase "
      + fromPhase.getId()
      + " to "
      + toPhase.getId()
      + " duration:"
      + duration
      + " "
      + switchingTimeList.toString();
  }
}
