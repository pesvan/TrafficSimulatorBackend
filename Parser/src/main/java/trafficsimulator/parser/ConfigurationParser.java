package trafficsimulator.parser;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author z003ru0y
 * Takes XML and returns internal representation of infrastructure
 */
public class ConfigurationParser
{
  private static Logger logger = LoggerFactory.getLogger(ConfigurationParser.class);
  
  /**
   * generic constructor
   * @param legLength length of a leg
   */
  public ConfigurationParser(double legLength)
  {
    logger.debug("Configuration Parser started");
    logger.warn("Configuration Parser is not available in the public version");
  }

}
