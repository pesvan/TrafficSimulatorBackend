package trafficsimulator.shared.dto;

import java.util.List;

import lombok.Data;

/**
 * Abstract DTO for Signal programs with some basic fields
 * @author z003ru0y
 *
 */
@Data
public abstract class SignalProgram
{  
  private final int id;    
  private final String programId;    
  private final int duration;  
  private final int activationOffset;
  private List<SumoPhase> sumoPhases;
  
  @Override
  public abstract String toString();

}
