package trafficsimulator.shared.dto;

import lombok.Data;

/**
 * DTO Switching time
 * @author z003ru0y
 *
 */
@Data
public class SwitchingTime
{  
  private final SignalGroup signalGroup;  
  private final int signal;  
  private final int time;

  @Override
  public String toString()
  {
    return "STime for SG " + signalGroup.getId()
      + " signal "
      + signal
      + " time "
      + time;
  }
}
