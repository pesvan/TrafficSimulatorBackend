package trafficsimulator.shared.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.Setter;
import trafficsimulator.shared.exceptions.LegValidationException;

/**
 * Another crazy class representing intersection leg
 * @author z003ru0y
 *
 */
public class Leg
{
  private static Logger logger = LoggerFactory.getLogger(Leg.class);

  
  @Getter
  private final String id;
  
  @Getter
  private final Angle angle;
  
  @Getter
  @Setter
  private Directions directions;
  
  @Getter
  private final Coordinates coordinates;
  
  @Getter
  private final String edgeInConnectionName;
  
  @Getter
  private final String edgeOutConnectionName;
  
  @Getter
  @Setter
  private Leg leftMostLeg = null;
  
  @Getter
  @Setter
  private Leg rightMostLeg = null;
  
  @Getter
  @Setter
  private Leg straightLeg = null;

  @Getter
  private List<SignalGroup> signalGroups;

  /**
   * @param id id of the leg
   * @param angle angle of the leg
   * @param coordinates coordinates of the beginning of the leg
   */
  public Leg(String id, Angle angle, Coordinates coordinates)
  {
    this.id = id;
    this.angle = angle;
    this.coordinates = coordinates;
    signalGroups = new ArrayList<>();
    directions = new Directions();
    edgeInConnectionName = id + "si";
    edgeOutConnectionName = id + "o";
  }

  /**
   * Updates direction of the leg 
   * @param sgDirections signal group directions
   */
  public void adjustDirections(Directions sgDirections)
  {
    if (sgDirections.isBack())
    {
      directions.setBack(true);
    }
    if (sgDirections.isLeft())
    {
      directions.setLeft(true);
    }
    if (sgDirections.isRight())
    {
      directions.setRight(true);
    }
    if (sgDirections.isStraight())
    {
      directions.setStraight(true);
    }
  }

  /**
   * adds new signal group to the leg
   * @param signalGroup signal group
   */
  public void addSignalGroup(SignalGroup signalGroup)
  {
    this.signalGroups.add(signalGroup);
  }

  /**
   * Calculates and sets possible left/right/straight most leg depending on the possible directions and intersection layout
   * @param legs all intersection legs
   */
  public void calculateTurns(List<Leg> legs)
  {
    int legCount = legs.size();
    List<Angle> angles = extractAngles(legs);
    if (this.directions.isStraightLeftRight())
    {
      // just 2 way intersection
      if (legCount == 2)
      {
        this.straightLeg = calculateStraightLeg(this, angles, legCount, legs);
      }
      else
      {
        this.leftMostLeg = calculateLeftMostLeg(angles, legs, this.angle);
        this.rightMostLeg = calculateRightMostLeg(angles, legs, this.angle);
        this.straightLeg = calculateStraightLeg(this, angles, legCount, legs);
      }

    }
    else if (this.directions.isStraightLeft() || this.directions.isStraightRight())
    {
      // typical T junction
      if (legCount == 3)
      {
        // going everywhere but back
        if (this.directions.isLeft())
        {
          // left and straight
          this.leftMostLeg = calculateLeftMostLeg(angles, legs, this.angle);
          this.straightLeg = calculateStraightLeg(this, angles, legCount, legs);
        }
        else
        {
          // right and straight
          this.rightMostLeg = calculateRightMostLeg(angles, legs, this.angle);
          this.straightLeg = calculateStraightLeg(this, angles, legCount, legs);
        }
      }
      else
      {
        if (this.directions.isStraightLeft())
        {
          // legCount is 4
          // going left and straight
          // lets find the most left leg and then second most left leg
          this.leftMostLeg = calculateLeftMostLeg(angles, legs, this.angle);
          this.straightLeg = calculateLeftMostLeg(angles, legs, this.leftMostLeg.getAngle());
        }
        else
        {
          // going right and straight
          // lets find the most right leg and then second most right leg          
          this.rightMostLeg = calculateRightMostLeg(angles, legs, this.angle);
          this.straightLeg = calculateRightMostLeg(angles, legs, this.rightMostLeg.getAngle());
        }
      }
    }
    else if (this.directions.isLeft() && this.directions.isRight())
    {
      // going just left or right
      this.leftMostLeg = calculateLeftMostLeg(angles, legs, this.angle);
      this.rightMostLeg = calculateRightMostLeg(angles, legs, this.angle);
    }
    else if (this.directions.isLeft())
    {
      // going only left
      this.leftMostLeg = calculateLeftMostLeg(angles, legs, this.angle);
    }
    else if (this.directions.isRight())
    {
      //  going only right
      this.rightMostLeg = calculateRightMostLeg(angles, legs, this.angle);
    }
    else if (this.directions.isStraight())
    {
      //  going only straight
      if (legCount == 4)
      {
        Leg tempLeftLeg = calculateLeftMostLeg(angles, legs, this.angle);
        this.straightLeg = calculateLeftMostLeg(angles, legs, tempLeftLeg.getAngle());
      }
      else
      {
        this.straightLeg = calculateStraightLeg(this, angles, legCount, legs);
      }

    }
  }

  /**
   * @return retrieves all the lanes of this leg from its signal groups
   */
  public List<Lane> getAllLanes()
  {
    List<Lane> allLanes = new ArrayList<>();
    for (SignalGroup sg : getSignalGroups())
    {
      for (Lane lane : sg.getLanes())
      {
        allLanes.add(lane);
      }
    }
    sortLanesByDirection(allLanes);
    return allLanes;
  }

  /**
   * Validates the leg for traffic inconsistencies 
   * @throws LegValidationException when there is some inconsistency 
   */
  public void validateLeg() throws LegValidationException
  {
    int leftOnlyCnt = 0;
    int rightOnlyCnt = 0;
    for (Lane lane : getAllLanes())
    {
      // There can be only one all-direction lane
      if (lane.getDirections().isStraightLeftRight() && getAllLanes().size() > 1)
      {
        throw new LegValidationException("Lane was not added, possible conflict");
      }
      if (lane.getDirections().isLeftRightOnly() && getAllLanes().size() > 1)
      {
        throw new LegValidationException("Lane was not added, possible conflict");
      }
      if (lane.getDirections().isLeftOnly())
      {
        leftOnlyCnt++;
        if (leftOnlyCnt > 2)
        {
          throw new LegValidationException("there can be only 2 pure left lanes");
        }
      }
      if (lane.getDirections().isRightOnly())
      {
        rightOnlyCnt++;
        if (rightOnlyCnt > 2)
        {
          throw new LegValidationException("there can be only 2 pure right lanes");
        }
      }
    }
  }

  @Override
  public String toString()
  {
    return "Leg " + angle
      + " "
      + edgeInConnectionName
      + (leftMostLeg == null ? ""
        : " left:" + leftMostLeg.getEdgeOutConnectionName())
      + (rightMostLeg == null ? ""
        : " right:" + rightMostLeg.getEdgeOutConnectionName())
      + (straightLeg == null ? ""
        : " straight:" + straightLeg.getEdgeOutConnectionName());
  }

  @Override
  public boolean equals(Object o)
  {
    Leg anotherLeg = (Leg)o;
    return this.id == anotherLeg.getId();
  }

  private static Leg calculateLeftMostLeg(List<Angle> angles, List<Leg> legs, Angle angle)
  {
    Angle leftMostAngle = angle;
    for (int i = 0, j = angle.getValue() + 1; i < 361; i++, j++)
    {
      if (j == 360)
      {
        j = 0;
      }
      for (int k = 0; k < angles.size(); k++)
      {
        if (angles.get(k).getValue().intValue() == j)
        {
          logger.info("(left it is)Angle: {}, i: {}, j: {}",angle.getValue(), i, j);
          leftMostAngle = new Angle(j);
          return getLegByAngle(legs, leftMostAngle);
        }
      }
      
    }
    return getLegByAngle(legs, leftMostAngle);
  }

  private static Leg calculateRightMostLeg(List<Angle> angles, List<Leg> legs, Angle angle)
  {
    Angle rightMostAngle = angle;
    for (int i = 360, j = angle.getValue() - 1; i >= 0; i--, j--)
    {
      if (j == -1)
      {
        j = 359;
      }
      
      for (int k = 0; k < angles.size(); k++)
      {
        if (angles.get(k).getValue().intValue() == j)
        {
          logger.info("(right it is)Angle: {}, i: {}, j: {}",angle.getValue(), i, j);
          rightMostAngle = new Angle(j);
          return getLegByAngle(legs, rightMostAngle);
        }
      }
      
    }
    return getLegByAngle(legs, rightMostAngle);
  }

  private static Leg calculateStraightLeg(Leg leg, List<Angle> angles, int legCount, List<Leg> legs)
  {
    Leg straight = null;
    for (Angle angle : angles)
    {
      if (legCount == 4)
      {
  
        if (!leg.getAngle().equals(angle) && !leg.getLeftMostLeg().getAngle().equals(angle)
          && !leg.getRightMostLeg().getAngle().equals(angle))
        {
          straight = getLegByAngle(legs, angle);
        }
      }
      else if (legCount == 3)
      {
        if (!leg.getAngle().equals(angle) &&
          ((leg.getLeftMostLeg() != null && !leg.getLeftMostLeg().getAngle().equals(angle))
            || (leg.getRightMostLeg() != null && !leg.getRightMostLeg().getAngle().equals(angle))))
        {
          straight = getLegByAngle(legs, angle);
        }
      }
      else if (legCount == 2)
      {
        if (!leg.getAngle().equals(angle))
        {
          straight = getLegByAngle(legs, angle);
        }
      }
      else
      {
        // TODO exception
      }
    }
  
    return straight;
  }

  private List<Angle> extractAngles(List<Leg> legs)
  {
    List<Angle> angles = new ArrayList<>();
    for (Leg leg : legs)
    {
      angles.add(leg.getAngle());
    }
    return angles;
  }

  private static Leg getLegByAngle(List<Leg> legs, Angle angle)
  {
    for (Leg leg : legs)
    {
      if (leg.getAngle().equals(angle))
      {
        return leg;
      }
    }
    return null;
  }

  /**
   * 
   */
  private void sortLanesByDirection(List<Lane> lanes)
  {
    Collections.sort(lanes);
  }


}
