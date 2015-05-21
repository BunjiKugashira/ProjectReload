/**
 *
 */
package modularity.events.errors;

/**
 * @author Alexander
 *
 */
public class SecurityErrorEvent extends ErrorEvent {
	/**
	 *
	 */
	public static final EventContainer container = new EventContainer();

	/**
	 * @param pEv
	 * @param pExc
	 */
	protected SecurityErrorEvent(final EventContainer pEv,
			final SecurityException pExc) {
		super(pEv, pExc);
	}

	/**
	 * @param pExc
	 */
	public SecurityErrorEvent(final SecurityException pExc) {
		super(container, pExc);
	}

}
