package trafficsimulator.simulator.output.dto;

import lombok.Data;

@Data
public class JsonPhaseState
{
  private final String programId;
  private final int id;
  private final int phaseId;
  private final double nextSwitch;
}
