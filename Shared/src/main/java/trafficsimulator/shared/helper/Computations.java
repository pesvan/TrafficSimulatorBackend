package trafficsimulator.shared.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import trafficsimulator.shared.dto.Angle;
import trafficsimulator.shared.dto.Coordinates;
import trafficsimulator.shared.dto.Flow;
import trafficsimulator.shared.dto.GridPosition;
import trafficsimulator.shared.dto.Intersection;
import trafficsimulator.shared.dto.IntersectionConnection;
import trafficsimulator.shared.dto.Leg;
import trafficsimulator.shared.dto.Situation;
import trafficsimulator.shared.enumerators.Direction;
import trafficsimulator.shared.exceptions.OccupiedGridPositionException;

/**
 * @author z003ru0y
 * Service which handles various intersection related computations
 */
public class Computations
{
  
  private final double distanceBetweenIntersections;
  
  private static Logger logger = LoggerFactory.getLogger(Computations.class);
  
  /**
   * @param distanceBetweenIntersections distance between two intersectons
   */
  public Computations(double distanceBetweenIntersections)
  {
    this.distanceBetweenIntersections = distanceBetweenIntersections;
    logger.info("Computations started");
  }

  /**
   * Searches the infrastructure for intersection which could be connected and connects them
   * @param situation source and output situation
   */
  public void connectIntersections(Situation situation)
  {
    for (Intersection intersection : situation.getIntersectionList())
    {
      for (Direction direction : Direction.values())
      {
        Intersection neighboor = getNeighboorIntersection(direction, intersection, situation.getIntersectionList());
        if (neighboor != null && 
          intersectionConnectionExists(intersection, neighboor, situation.getIntersectionConnections()))
        {
          IntersectionConnection connection =
            connectIntersectionsByDistance(intersection, neighboor, direction);
  
          //we dont want to have 3-and more sided connections
          if (connection != null &&
            !isLegInIntersectionConnection(connection.getLegI1(), situation.getIntersectionConnections()) && 
            !isLegInIntersectionConnection(connection.getLegI2(), situation.getIntersectionConnections()))
          {
            situation.addIntersectionConnection(connection);
          }
        }
      }
    }
    
    List<Flow> flows = retrieveRoutes(situation);
    
    situation.setRoutes(flows);
  }

  /**
   * Retrieves grid position next to the source intersection in desired direction
   * @param sourcePosition position of source intersection
   * @param direction direction to the next place
   * @return relative position
   */
  public GridPosition getRelativeGridPosition(GridPosition sourcePosition, Direction direction)
  {
    int srcX = sourcePosition.getX();
    int srcY = sourcePosition.getY();

    switch (direction)
    {
      case BOTTOM:
        return new GridPosition(srcX, srcY - 1);
      case LEFT:
        return new GridPosition(srcX - 1, srcY);
      case TOP:
        return new GridPosition(srcX, srcY + 1);
      case RIGHT:
        return new GridPosition(srcX + 1, srcY);
    }
    
    return null;
  }

  /**
   * Generates coordinates based on grid position and application properties
   * @param gridPosition grid position of the intersection
   * @return Coordinates x,y
   */
  public Coordinates gridPositionToCoordinates(Optional<GridPosition> gridPosition)
  {
    return gridPosition
      .map(pos -> new Coordinates(pos.getX() * distanceBetweenIntersections,
        pos.getY() * distanceBetweenIntersections))
      .orElseGet(() -> new Coordinates(0.0, 0.0));
  }

  /**
   * Tests if the position is not occupied
   * @param gridPosition position for test
   * @param intersectionList list of intersection
   * @throws OccupiedGridPositionException when the position is not free
   */
  public void assertGridPositionIsFree(GridPosition gridPosition, List<Intersection> intersectionList) 
      throws OccupiedGridPositionException
  {
    if (getIntersectionByGridPos(gridPosition, intersectionList) != null)
    {
      throw new OccupiedGridPositionException(gridPosition.toString());
    }
  }

  /**
   * Creates intersection connection between two intersections.
   * Connects two legs which are closest
   * @param intersection1 intersection to connect
   * @param intersection2 other intersection to connect
   * @param direction for checking if the connection is allowed in that direction
   * @return IntersectionConnection or null if the shortest connection is not possible
   */
  private IntersectionConnection connectIntersectionsByDistance(
      Intersection intersection1, Intersection intersection2, Direction direction)
  {
    double middleX = (intersection1.getCoordinates().getX() + intersection2.getCoordinates().getX()) / 2;
    double middleY = (intersection1.getCoordinates().getY() + intersection2.getCoordinates().getY()) / 2;
    Coordinates middleCoordinates = new Coordinates(middleX, middleY);
    Leg i1closest = null;
    double shortestDistance = Double.POSITIVE_INFINITY;
    double distance = 0;
    for (Leg leg : intersection1.getLegs())
    {
      distance = getDistance(leg.getCoordinates(), middleCoordinates);
      if (distance < shortestDistance)
      {
        shortestDistance = distance;
        i1closest = leg;
      }
    }
    Leg i2closest = null;
    shortestDistance = Double.POSITIVE_INFINITY;
    for (Leg leg : intersection2.getLegs())
    {
      distance = getDistance(leg.getCoordinates(), middleCoordinates);
      if (distance < shortestDistance)
      {
        shortestDistance = distance;
        i2closest = leg;
      }
    }
    
    if (isAllowedConnectionAngle(direction, i1closest.getAngle(), i2closest.getAngle()))
    {
      String id = "con" + intersection1.getId()
      + "-"
      + intersection2.getId();
      return new IntersectionConnection(id, intersection1, intersection2, i1closest, i2closest);
    }
    else
    {
      return null;
    }
  }

  private boolean isAllowedConnectionAngle(Direction direction, Angle angle1, Angle angle2)
  {
    logger.debug("Allowed connection? Direction: {}, src angle: {}, dest angle {}", direction, angle1, angle2);
    
    switch (direction)
    {
      case BOTTOM:
        return (angle1.getValue() >= 315 || angle1.getValue() <= 45) && (angle2.getValue() >= 135 && angle2.getValue() <= 225);
      case TOP:
        return (angle2.getValue() >= 315 || angle2.getValue() <= 45) && (angle1.getValue() >= 135 && angle1.getValue() <= 225);
      case LEFT:
        return (angle1.getValue() >= 45 && angle1.getValue() <= 135) && (angle2.getValue() >= 225 && angle2.getValue() <= 315);
      case RIGHT:
        return (angle2.getValue() >= 45 && angle2.getValue() <= 135) && (angle1.getValue() >= 225 && angle1.getValue() <= 315);
      default:
        return false;
    }
  }


  private Intersection getIntersectionByGridPos(GridPosition gridPosition, List<Intersection> intersectionList)
  {
    for (Intersection intersection : intersectionList)
    {
      if (gridPosition.equals(intersection.getGridPosition()))
      {
        return intersection;
      }
    }
    
    return null;
  }
  
  private Intersection getNeighboorIntersection(Direction direction, Intersection srcIntersection, 
      List<Intersection> intersectionList)
  {

    GridPosition neighboorPosition =
      getRelativeGridPosition(srcIntersection.getGridPosition(), direction);

    return getIntersectionByGridPos(neighboorPosition, intersectionList);
  }
  

  private boolean intersectionConnectionExists(Intersection intersection1, Intersection intersection2, 
      List<IntersectionConnection> intersectionConnections)
  {
    for (IntersectionConnection iConnection : intersectionConnections)
    {
      if (iConnection.getIntersection1().equals(intersection1) && iConnection.getIntersection2().equals(intersection2)
        || iConnection.getIntersection2().equals(intersection1) && iConnection.getIntersection1().equals(intersection2))
      {
        return true;
      }
    }
    return false;
  }
  

  private boolean isLegInIntersectionConnection(Leg leg, List<IntersectionConnection> intersectionConnections)
  {
    for (IntersectionConnection iConnection : intersectionConnections)
    {
      if (iConnection.getLegI1().equals(leg) || iConnection.getLegI2().equals(leg))
      {
        return true;
      }
    }
    return false;
  }
  

  private List<Leg> getEndLegs(Situation situation)
  {
    List<Leg> endLegs = new ArrayList<>();
    for (Intersection intersection : situation.getIntersectionList())
    {
      for (Leg leg : intersection.getLegs())
      {
        if (!isLegInIntersectionConnection(leg, situation.getIntersectionConnections()))
        {
          endLegs.add(leg);
        }
      }
    }
    return endLegs;
  }
  
  private List<Flow> retrieveRoutes(Situation situation)
  {
    List<Leg> endLegs = getEndLegs(situation);
    List<Flow> routes = new ArrayList<>();

    for (Leg leg : endLegs)
    {
      for (Leg outputLeg : endLegs)
      {
        if (!leg.equals(outputLeg))
        {
          routes.add((new Flow(leg, outputLeg)));
        }
      }
    }
    
    return routes;
  }

  private double getDistance(Coordinates c1, Coordinates c2)
  {
    return Math.sqrt((c1.getX() - c2.getX()) * (c1.getX() - c2.getX()))
      + ((c1.getY() - c2.getY()) * (c1.getY() - c2.getY()));
  }
  
}
