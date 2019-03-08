package trafficsimulator.shared.dto;

import lombok.Getter;

/**
 * Angle class represents a value withing limitation - valid is from 0 to 359
 */
public class Angle
{
  @Getter
  private Integer value;

  /**
   * @param angle initial angle
   */
  public Angle(int angle)
  {
    if (angle >= 360 || angle < 0)
    {
      throw new IllegalArgumentException("Angle has to be number between 0 and 359, was " + angle);
    }
    value = angle;
  }

  /**
   * Adds positive value to the angle. The resulting value is always in 0 to 359 interval
   * @param addition Value to be added to the angle
   */
  public void addToAngle(int addition)
  {
    if(addition < 0)
    {
      throw new IllegalArgumentException("Addition has to be positive number, was " + addition);
    }
    int i, j;
    for (i = value, j = 0; j < addition; i++, j++)
    {
      if (i == 360)
      {
        i = 0;
      }
    }
    if(i == 360)
    {
      i = 0;
    }
    value = i;
  }
  
  @Override
  public boolean equals(Object a)
  {
    Angle other = (Angle) a;
    return other.getValue().intValue() == value;
  }

  @Override
  public String toString()
  {
    return "Angle [value=" + value
      + "]";
  }

  
  
}
