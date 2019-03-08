package trafficsimulator.shared.dto;

import lombok.Getter;

/**
 * DTO directions
 * @author z003ru0y
 */
public class Directions implements Comparable<Directions>
{
  private boolean straight = false;
  private boolean left = false;
  private boolean right = false;
  private boolean back = false;

  @Getter
  private Integer sortingIndex;

   /**
   * @param straight straight direction
   * @param left left direction
   * @param right right direction
   * @param back back direction
   */
  public Directions(boolean straight, boolean left, boolean right, boolean back)
  {
    this.straight = straight;
    this.left = left;
    this.right = right;
    this.back = back;
    this.sortingIndex = this.setSortingIndex();
  }

  /**
   * generic constructor
   */
  public Directions()
  {
  }

  /**
   * @return true if all three directions are there
   */
  public boolean isStraightLeftRight()
  {
    return this.straight && this.left
      && this.right;
  }

  /**
   * @return true if directions allow straight
   */
  public boolean isStraight()
  {
    return this.straight;
  }

  /**
   * @return true if directions allow left
   */
  public boolean isLeft()
  {
    return this.left;
  }

  /**
   * @return true if directions allow right
   */
  public boolean isRight()
  {
    return this.right;
  }

  /**
   * @return true if directions allow straight and left
   */
  public boolean isStraightLeft()
  {
    return this.isStraight() && this.isLeft();
  }

  /**
   * @return true if directions allow straight and right
   */
  public boolean isStraightRight()
  {
    return this.isStraight() && this.isRight();
  }

  /**
   * @return true if directions allow back turn
   */
  public boolean isBack()
  {
    return this.back;
  }

  /**
   * @return true if directions allows only left
   */
  public boolean isLeftOnly()
  {
    return this.left && !this.straight
      && !this.right;
  }

  /**
   * @return true if directions allows only right
   */
  public boolean isRightOnly()
  {
    return !this.left && !this.straight
      && this.right;
  }

  /**
   * @return true if directions allows only left or right
   */
  public boolean isLeftRightOnly()
  {
    return this.left && !this.straight
      && this.right;
  }

  /**
   * @return number of possible connections from a node
   */
  public int getConnectionsCount()
  {
    if (isStraightLeftRight())
    {
      return 3;
    }
    else if (isStraightLeft() || isStraightRight())
    {
      return 2;
    }
    else
      return 1;
  }

  /**
   * Set the left value and does the sorting index
   * @param value left
   */
  public void setLeft(boolean value)
  {
    this.left = value;
    setSortingIndex();
  }

  /**
   * Set the straight value and does the sorting index
   * @param value straight
   */
  public void setStraight(boolean value)
  {
    this.straight = value;
    setSortingIndex();
  }

  /**
   * Set the right value and does the sorting index
   * @param value right
   */
  public void setRight(boolean value)
  {
    this.right = value;
    setSortingIndex();
  }

  /**
   * Set the back value and does the sorting index
   * @param value back
   */
  public void setBack(boolean value)
  {
    this.back = value;
  }


  @Override
  public String toString()
  {
    return "Directions [straight=" + straight
      + ", left="
      + left
      + ", right="
      + right
      + ", back="
      + back
      + ", sortingIndex="
      + sortingIndex
      + "]";
  }

  @Override
  public int compareTo(Directions anotherDirection)
  {
    return this.sortingIndex.compareTo(anotherDirection.sortingIndex);
  }

  private int setSortingIndex()
  {
    if (isStraightLeftRight())
    {
      return 6;
    }
    if (isLeftOnly())
    {
      return 1;
    }
    if (isStraightLeft())
    {
      return 2;
    }
    if (isStraight() && !isRight())
    {
      return 3;
    }
    if (isStraightRight())
    {
      return 4;
    }
    if (isRightOnly())
    {
      return 5;
    }
    return 0;
  }
  
  /**
   * @return true if the directions has no forward direction
   */
  public boolean hasNoDirection()
  {
    return !left && !right && !straight;
  }

}
