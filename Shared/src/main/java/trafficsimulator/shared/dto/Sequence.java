package trafficsimulator.shared.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO used in parsing signal programs
 * Sequence represents signal of one signal group on intersection
 * @author z003ru0y
 *
 */
public class Sequence
{
  @Getter
  @Setter
  private int[] signalSeq;
  
  @Getter
  @Setter
  private Phase[] phaseSeq;

  /**
   * @param duration duration of signal program
   */
  public Sequence(int duration)
  {
    this.signalSeq = new int[duration];
    this.phaseSeq = new Phase[duration];
    for (int i = 0; i < duration; i++)
    {
      signalSeq[i] = -1;
      phaseSeq[i] = null;
    }
  }

  /**
   * @return true if the sequence is completed
   */
  public boolean isComplete()
  {
    for (int i = 0; i < signalSeq.length; i++)
    {
      if (signalSeq[i] == -1)
      {
        return false;
      }
    }
    return true;
  }

}
