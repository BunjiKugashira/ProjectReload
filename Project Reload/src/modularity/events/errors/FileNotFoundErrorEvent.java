/**
 *
 */
package modularity.events.errors;

import java.io.FileNotFoundException;

/**
 * @author Alexander
 *
 */
public class FileNotFoundErrorEvent extends ErrorEvent {
	/**
	 *
	 */
	public static final EventContainer container = new EventContainer();

	/**
	 * @param pEv
	 * @param pExc
	 */
	protected FileNotFoundErrorEvent(final EventContainer pEv,
			final FileNotFoundException pExc) {
		super(pEv, pExc);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param pExc
	 */
	public FileNotFoundErrorEvent(final FileNotFoundException pExc) {
		super(container, pExc);
		// TODO Auto-generated constructor stub
	}

}
