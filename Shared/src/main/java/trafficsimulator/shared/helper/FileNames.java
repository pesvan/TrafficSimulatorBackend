package trafficsimulator.shared.helper;

import lombok.Data;

/**
 * @author z003ru0y
 * Contains sumo configuration file names
 * more described in application.properties
 */
@Data
public class FileNames
{
  private String nodesFileName;
  private String edgesFileName;
  private String connectionsFileName;
  private String networkConfFileName;
  private String networkFileName;
  private String routesFileName;
  private String routesTempFileName;    
  private String flowsFileName;
  private String tlsFileName;    
  private String detFileName;
  private String sumoConfigurationFileName;
}