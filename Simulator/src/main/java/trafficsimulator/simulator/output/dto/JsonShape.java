package trafficsimulator.simulator.output.dto;

import java.util.List;

import lombok.Data;

@Data
public class JsonShape
{
  private final List<JsonCoordinates> coords;
}
