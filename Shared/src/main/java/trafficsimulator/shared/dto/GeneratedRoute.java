package trafficsimulator.shared.dto;

import lombok.Data;

/**
 * DTO for generated routes
 * edges = string of edges of the route
 * @author z003ru0y
 *
 */
@Data
public class GeneratedRoute
{  
  private final String id;  
  private final String edges;
}
