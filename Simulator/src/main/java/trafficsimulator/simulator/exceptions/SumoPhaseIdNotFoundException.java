package trafficsimulator.simulator.exceptions;

/**
 * @author z003ru0y
 *
 */
public class SumoPhaseIdNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;
	
	/**
     * @param message error message
	 */
	public SumoPhaseIdNotFoundException(String message) {
        super(message);
    }
}
