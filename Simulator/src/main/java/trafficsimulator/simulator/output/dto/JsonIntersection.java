package trafficsimulator.simulator.output.dto;

import java.util.List;

import lombok.Getter;
import trafficsimulator.shared.dto.GridPosition;

public class JsonIntersection
{

  @Getter
  private final String id;
  
  @Getter
  private final JsonShape shape;
  
  @Getter
  private final JsonGridPosition gridPosition;

  @Getter
  private final List<JsonIntersectionLeg> legList;
  
  @Getter
  private final List<String> signalProgramList;
  
  @Getter
  private final int angle;

  public JsonIntersection(
    String id, 
    JsonShape shape,
    GridPosition gridPos, 
    int angle, 
    List<JsonIntersectionLeg> legList,
    List<String> signalProgramList)
  {
    this.id = id;
    this.shape = shape;
    this.gridPosition = new JsonGridPosition(gridPos.getX(), gridPos.getY());
    this.angle = angle;
    this.legList = legList;
    this.signalProgramList = signalProgramList;
  }
}
