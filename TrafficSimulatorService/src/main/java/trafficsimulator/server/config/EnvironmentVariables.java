package trafficsimulator.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import lombok.Getter;
import trafficsimulator.shared.helper.FileNames;

/**
 * @author z003ru0y
 * Reads environment variables from application.properties file
 */
public class EnvironmentVariables
{
  private static Logger logger = LoggerFactory.getLogger(EnvironmentVariables.class);

  
  @Getter
  private final String sumoConfigurationPath;
    
  @Getter
  private final int maxVehicles;
  
  @Getter
  private final double simulationStepLength;
  
  @Getter
  private final double distanceBetweenIntersections;
  
  @Getter
  private final double legLength;
  
  @Getter
  private final int visualizationMultiplier;
  
  @Getter
  private final FileNames files;
  
  /**
   * @param env spring environment
   */
  public EnvironmentVariables(Environment env)
  {
    sumoConfigurationPath = env.getProperty("trafficsimulator.generator.confpath");
        
    maxVehicles = Integer.parseInt(env.getProperty("trafficsimulator.simulator.maxvehicles"));    

    simulationStepLength = Double.parseDouble(env.getProperty("trafficsimulator.simulator.simsteplength"));
    
    distanceBetweenIntersections = Double.parseDouble(env.getProperty("trafficsimulator.generator.distance"));
    
    legLength = Double.parseDouble(env.getProperty("trafficsimulator.generator.leglength"));
    
    visualizationMultiplier = Integer.parseInt(env.getProperty("trafficsimulator.server.multiplier"));
    
    files = parseFiles(env);
    
    logger.info("Environment variables from appliaction.properties below.");
    logger.info("Sumo configuration path: {}", sumoConfigurationPath);
    logger.info("Maximum number of vehicles: {}", maxVehicles);
    logger.info("Simulation step length[s]: {}", simulationStepLength);
    
    logger.debug("Environment variables initialized");
  }

  private FileNames parseFiles(Environment env)
  {
    FileNames files = new FileNames();
    
    files.setNodesFileName(env.getProperty("trafficsimulator.generator.file.nodes"));
    files.setEdgesFileName(env.getProperty("trafficsimulator.generator.file.edges"));
    files.setConnectionsFileName(env.getProperty("trafficsimulator.generator.file.connections"));
    files.setNetworkConfFileName(env.getProperty("trafficsimulator.generator.file.network.conf"));
    files.setNetworkFileName(env.getProperty("trafficsimulator.generator.file.network"));
    files.setRoutesFileName(env.getProperty("trafficsimulator.generator.file.routes"));
    files.setRoutesTempFileName(env.getProperty("trafficsimulator.generator.file.routes.temp"));
    files.setFlowsFileName(env.getProperty("trafficsimulator.generator.file.flows"));
    files.setTlsFileName(env.getProperty("trafficsimulator.generator.file.tls"));
    files.setDetFileName(env.getProperty("trafficsimulator.generator.file.det"));
    files.setSumoConfigurationFileName(env.getProperty("trafficsimulator.generator.file.configuration"));
    
    return files;
  }
}
