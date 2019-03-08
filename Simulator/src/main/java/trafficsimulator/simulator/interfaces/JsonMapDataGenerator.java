package trafficsimulator.simulator.interfaces;

import trafficsimulator.simulator.exceptions.TraciException;
import trafficsimulator.simulator.output.dto.JsonSituationLayout;

/**
 * Interface responsible for generating map data for client in json format
 * @author z003ru0y
 *
 */
public interface JsonMapDataGenerator
{
  /**
   * Generates all the json data required for the client to show
   * @param visualizationMultiplier multiplier for vehicle dimensions
   * @return object containing all necessary map data
   * @throws TraciException when traci call fails
   */
  JsonSituationLayout generateMapData(int visualizationMultiplier) throws TraciException;
}
