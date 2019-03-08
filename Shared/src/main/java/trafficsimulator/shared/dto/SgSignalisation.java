package trafficsimulator.shared.dto;

import lombok.Data;

/**
 * DTO containing signal group times for changing between green/red times
 * @author z003ru0y
 *
 */
@Data
public class SgSignalisation
{

  private final int transitionGreenRed;
  private final int transitionRedGreen;


  @Override
  public String toString()
  {
    return "[TransGreenRed:" + this.transitionGreenRed
      + ",TransRedGreen:"
      + this.transitionRedGreen
      + "]";
  }

}
