/**
 *
 */
package modularity.events.errors;

import modularity.Reaction;
import modularity.events.Event;
import modularity.events.NonThrowingEvent;

/**
 * @author Alexander
 *
 */
public abstract class ErrorEvent extends NonThrowingEvent {
	/**
	 *
	 */
	public static final EventContainer container = new EventContainer();
	private final Exception _exc;

	/**
	 * Instantiates a new error event. TODO comment
	 *
	 * @param pEv
	 *
	 * @param pExc
	 *            the exc
	 */
	public ErrorEvent(final EventContainer pEv, final Exception pExc) {
		super(pEv);
		_exc = pExc;
	}

	/**
	 * Instantiates a new error event. TODO comment
	 *
	 * @param pExc
	 *            the exc
	 */
	public ErrorEvent(final Exception pExc) {
		super(container);
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
		container.registerReaction("ErrorEvent.throwException", new Reaction() {

			@Override
			public void react(final Event pThis) {
				getException().printStackTrace();
			}
		}, 2);
	}
}
