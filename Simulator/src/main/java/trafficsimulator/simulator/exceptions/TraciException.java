package trafficsimulator.simulator.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.polito.appeal.traci.SumoTraciConnection;

/**
 * @author z003ru0y
 *
 */
public class TraciException extends Exception
{

  private static final long serialVersionUID = 1L;
  
  private static Logger logger = LoggerFactory.getLogger(TraciException.class);

  /**
   * @param e originalException
   * @param conn traci connection for printing out sumo error
   * @param msg error message
   */
  public TraciException(Exception e, SumoTraciConnection conn, String msg)
  {
    super(e);
    logger.error(msg);
    conn.printSumoError(true);       
  }
}
