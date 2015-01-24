/**
 *
 */
package modularity.events.errors;

import java.util.concurrent.TimeoutException;

/**
 * @author Alexander
 *
 */
public class TimeoutErrorEvent extends ErrorEvent {

	/**
	 * @param pExc
	 */
	public TimeoutErrorEvent(final TimeoutException pExc) {
		super(pExc);
	}

	@Override
	public TimeoutException getException() {
		return (TimeoutException) super.getException();
	}

	@Override
	public void run() throws TimeoutException {
		try {
			super.run();
		} catch (final Exception e) {
			throw (TimeoutException) e;
		}
	}

}
