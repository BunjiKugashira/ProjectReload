/**
 *
 */
package modularity.events.errors;

import modularity.events.Event;

/**
 * @author Alexander
 *
 */
public abstract class ErrorEvent extends Event {
	private final Exception _exc;

	/**
	 * Instantiates a new error event. TODO comment
	 *
	 * @param pExc
	 *            the exc
	 */
	public ErrorEvent(final Exception pExc) {
		super();
		_exc = pExc;
	}

	/**
	 * @return the _exc
	 */
	public Exception getException() {
		return _exc;
	}
}
