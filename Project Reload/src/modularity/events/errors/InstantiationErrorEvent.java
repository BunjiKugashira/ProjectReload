/**
 *
 */
package modularity.events.errors;

import java.lang.InstantiationException;

/**
 * @author Alexander
 *
 */
public class InstantiationErrorEvent extends ErrorEvent {
	/**
	 *
	 */
	public static final EventContainer container = new EventContainer();

	/**
	 * @param pEv
	 * @param pExc
	 */
	protected InstantiationErrorEvent(final EventContainer pEv,
			final InstantiationException pExc) {
		super(pEv, pExc);
	}

	/**
	 * @param pExc
	 */
	public InstantiationErrorEvent(final InstantiationException pExc) {
		super(container, pExc);
	}

}
