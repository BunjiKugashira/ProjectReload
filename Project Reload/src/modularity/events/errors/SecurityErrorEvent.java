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
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param pExc
	 */
	public SecurityErrorEvent(final SecurityException pExc) {
		super(container, pExc);
		// TODO Auto-generated constructor stub
	}

}
