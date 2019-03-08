package trafficsimulator.shared.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * Detector DTO
 * @author z003ru0y 
 */
public class Detector
{
  @Getter
  private final String id;

  @Getter
  private List<Lane> lanesUsed;

  @Getter
  private final int runningGap;
  
  @Getter
  private final int demandResetThreshold;

  /**
   * @param runningGap  a
   * @param demandResetThreshhold  n
   * @param id id of detector
   */
  public Detector(int runningGap, int demandResetThreshhold, String id)
  {
    this.runningGap = runningGap;
    this.demandResetThreshold = demandResetThreshhold;
    this.id = id
      + "-"
      + runningGap
      + "-"
      + demandResetThreshhold;
    this.lanesUsed = new ArrayList<>();
  }

  /**
   * Lanes which are using the detector
   * @param lanesUsed lane
   */
  public void addLaneUsed(Lane lanesUsed)
  {
    this.lanesUsed.add(lanesUsed);
  }

  @Override
  public String toString()
  {
    return "Detector [id=" + id
      + ", lanesUsed="
      + lanesUsed
      + ", runningGap="
      + runningGap
      + ", demandResetThreshold="
      + demandResetThreshold
      + "]";
  }

}
