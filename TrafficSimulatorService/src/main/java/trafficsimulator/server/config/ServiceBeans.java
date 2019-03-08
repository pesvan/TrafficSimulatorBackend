package trafficsimulator.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import trafficsimulator.generator.SumoNetworkConfigurationGenerator;
import trafficsimulator.generator.XmlGenerator;
import trafficsimulator.parser.ConfigurationParser;
import trafficsimulator.server.session.SessionMemory;
import trafficsimulator.shared.helper.Computations;
import trafficsimulator.simulator.control.SimulationControlFactory;
import trafficsimulator.simulator.traci.TraciService;
import trafficsimulator.simulator.traci.impl.TraciServiceImpl;

/**
 * @author z003ru0y
 * Starts all the important beans at the startup of application
 */
@Configuration
public class ServiceBeans
{

  @Autowired
  private Environment env;

  /**
   * Place where all the application properties are stored and accessible
   * @return EnvironmentVariables instance
   */
  @Bean
  public EnvironmentVariables variables()
  {
    return new EnvironmentVariables(env);
  }
  
  /**
   * Helper class for various computations
   * @param vars EnvironmentVariables instance
   * @return Computations instance
   */
  @Bean
  public Computations createComputationsHelper(EnvironmentVariables vars)
  {
    return new Computations(vars.getDistanceBetweenIntersections());
  }

  /**
   * Takes XML and returns internal representation of infrastructure
   * @param vars EnvironmentVariables instance
   * @return ConfigurationParser instance
   */
  @Bean
  public ConfigurationParser createParserInstance(EnvironmentVariables vars)
  {
    return new ConfigurationParser(vars.getLegLength());
  }

  /**
   * Helper class for the generator
   * @param vars EnvironmentVariables instance
   * @return XmlGenerator instance
   */
  @Bean
  public XmlGenerator createXmlGenerator(EnvironmentVariables vars)
  {
    return new XmlGenerator(vars.getFiles());
  }

  /**
   * Generates XML configuration files for SUMO
   * @param vars EnvironmentVariables instance
   * @return SumoNetworkConfigurationGenerator instance
   */
  @Bean
  public SumoNetworkConfigurationGenerator createSumoXmlGenerator(EnvironmentVariables vars)
  {
    return new SumoNetworkConfigurationGenerator(vars.getSumoConfigurationPath(), vars.getFiles());
  }

  /**
   * Generates new SimulationControl instances when needed
   * @param vars EnvironmentVariables instance
   * @return SimulationControlFactory instance
   */
  @Bean
  public SimulationControlFactory createSimulationRunnerFactory(EnvironmentVariables vars)
  {
    return new SimulationControlFactory(vars.getSimulationStepLength(), vars.getMaxVehicles());
  }
  
  /**
   * TraciService for communicating with SUMO
   * @param vars EnvironmentVariables instance
   * @return TraciService instance
   */
  @Bean
  public TraciService createTraciService(EnvironmentVariables vars)
  {
    return new TraciServiceImpl(
        vars.getSumoConfigurationPath(), vars.getSimulationStepLength(), vars.getFiles().getSumoConfigurationFileName());
  }

  /**
   * Contains current relevant data about ongoing simulation
   * @param vars EnvironmentVariables instance
   * @return SessionMemory instance
   */
  @Bean
  public SessionMemory createSessionMemoryInstance(EnvironmentVariables vars)
  {
    return new SessionMemory(vars.getVisualizationMultiplier());
  }

  
}
