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

}
