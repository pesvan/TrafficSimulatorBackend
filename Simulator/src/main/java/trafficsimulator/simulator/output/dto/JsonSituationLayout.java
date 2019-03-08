package trafficsimulator.simulator.output.dto;

import java.util.List;

import lombok.Data;

@Data
public class JsonSituationLayout
{
  private final JsonLayoutMetadata metadata;
  private final List<JsonIntersection> intersectionList;
  private final List<JsonConnectionLeg> connectionLegs;
  private final List<JsonConnectionPolygon> connectionPolygons;
  private final List<JsonVehicle> alreadyExistingVehicles;
}
