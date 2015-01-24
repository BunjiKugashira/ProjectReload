/**
 *
 */
package modularity.events.errors;

import modularity.ThrowingReaction;
import modularity.events.Event;
import modularity.events.ThrowingEvent;

/**
 * @author Alexander
 *
 */
public abstract class ErrorEvent extends ThrowingEvent {
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

	@Override
	protected void registerEventspecificReactions() {
		registerReaction("ErrorEvent.throwException", new ThrowingReaction() {

			@Override
			public void react(final Event pThis) throws Exception {
				throw _exc;
			}

		}, 1);
	}
}
