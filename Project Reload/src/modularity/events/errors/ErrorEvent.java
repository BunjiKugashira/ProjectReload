/**
 *
 */
package modularity.events.errors;

import modularity.ReactionOld;
import modularity.events.EventOld;
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
	private final EventContainer _cont;
	private final Exception _exc;

	/**
	 * Instantiates a new error event. TODO comment
	 *
	 * @param pEv
	 *
	 * @param pExc
	 *            the exc
	 */
	protected ErrorEvent(final EventContainer pEv, final Exception pExc) {
		super(pEv);
		_cont = pEv;
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
		_cont = container;
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
		_cont.registerReaction("ErrorEvent.throwException", new ReactionOld() {

			@Override
			public void react(final EventOld pThis) {
				getException().printStackTrace();
			}
		}, 2);
	}
}
