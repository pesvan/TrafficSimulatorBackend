package trafficsimulator.shared.dto;

import lombok.Data;

/**
 * DTO for grid position [x,y]
 * @author z003ru0y
 *
 */
@Data
public class GridPosition
{
  private final int x;  
  private final int y;


  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + x;
    result = prime * result + y;
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (!(obj instanceof GridPosition))
    {
      return false;
    }
    GridPosition other = (GridPosition)obj;
    if (x != other.x)
    {
      return false;
    }
    if (y != other.y)
    {
      return false;
    }
    return true;
  }

  @Override
  public String toString()
  {
    return "[x=" + x
      + ", y="
      + y
      + "]";
  }

}
