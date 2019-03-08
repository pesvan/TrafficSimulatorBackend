package trafficsimulator.shared.dto;

import lombok.Data;

/**
 * Representing sumo phase
 * @author z003ru0y
 *
 */
@Data
public class SumoPhase
{  
  private final int id;  
  private final int duration;  
  private final String state;  
  private final Phase phase;

  @Override
  public String toString()
  {
    return "SumoPhase [duration=" + duration
      + ", state="
      + state
      + ", phase"
      + (phase == null ? ""
        : phase.getId())
      + "]";
  }
}
