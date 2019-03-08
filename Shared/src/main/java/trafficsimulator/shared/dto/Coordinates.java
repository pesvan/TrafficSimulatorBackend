package trafficsimulator.shared.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * Class responsible for storing coordinates for general purposes
 */
public class Coordinates
{
  @Getter @Setter
  private double x;

  @Getter @Setter
  private double y;

  /**
   * Basic constructor
   * @param x x-value
   * @param y y-value
   */
  public Coordinates(double x, double y)
  {
    this.x = roundSmallNumberToZero(x);
    this.y = roundSmallNumberToZero(y);
  }

  /**
   * Move the coordinates in specified direction by specified offset
   * @param angle Direction in which to move the point
   * @param distance Distance to move
   * @return new set of moved coordinates
   */
  public Coordinates moveByDistance(Angle angle, double distance)
  {
    double degreeAngle = Math.toRadians(angle.getValue());
    double x = this.x + distance * Math.sin(degreeAngle) * -1;
    double y = this.y + distance * Math.cos(degreeAngle) * -1;
    return new Coordinates(x, y);
  }

  private static double roundSmallNumberToZero(double value)
  {
    return (value < 0.01 && value > -0.01) ? 0.0 : value;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Coordinates that = (Coordinates) o;
    return Double.compare(that.x, x) == 0 &&
      Double.compare(that.y, y) == 0;
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(x, y);
  }

  @Override
  public String toString()
  {
    return "[" + this.x
      + ","
      + this.y
      + "]";
  }
}
