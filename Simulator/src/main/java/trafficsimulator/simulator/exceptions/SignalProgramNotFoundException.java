package trafficsimulator.simulator.exceptions;

/**
 * @author z003ru0y
 * 
 */
public class SignalProgramNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;
	
	/**
	 * @param message error message
	 */
	public SignalProgramNotFoundException(String message) {
        super(message);
    }
}
