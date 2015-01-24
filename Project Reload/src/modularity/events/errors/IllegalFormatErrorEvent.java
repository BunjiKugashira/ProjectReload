/**
 *
 */
package modularity.events.errors;

import java.util.IllegalFormatException;

/**
 * @author Alexander
 *
 */
public class IllegalFormatErrorEvent extends ErrorEvent {

	/**
	 * @param pExc
	 */
	public IllegalFormatErrorEvent(final IllegalFormatException pExc) {
		super(pExc);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() throws IllegalFormatException {
		try {
			super.run();
		} catch (final Exception e) {
			throw (IllegalFormatException) e;
		}
	}

}