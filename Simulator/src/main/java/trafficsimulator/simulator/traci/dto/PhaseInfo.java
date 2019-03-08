package trafficsimulator.simulator.traci.dto;

import lombok.Data;

/**
 * Simple DTO for transfering information about phases from SUMO
 * @author z003ru0y
 *
 */
@Data
public class PhaseInfo
{
  private final String sequence;
  private final double duration;

  /**
   * @param sequence phase sequence (like "GgRr..")
   * @param duration duration of the phase
   */
  public PhaseInfo(String sequence, double duration)
  {
    this.sequence = sequence;
    this.duration = duration;
  }
}
