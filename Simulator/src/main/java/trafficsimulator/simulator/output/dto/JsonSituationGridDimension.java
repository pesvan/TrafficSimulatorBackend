package trafficsimulator.simulator.output.dto;

import lombok.Data;

@Data
public class JsonSituationGridDimension
{
  private int minimumX;
  private int minimumY;
  private int maximumX;
  private int maximumY;
  
  public JsonSituationGridDimension()
  {
    minimumX = 0;
    minimumY = 0;
    maximumX = 0;
    maximumY = 0;
  }
}
