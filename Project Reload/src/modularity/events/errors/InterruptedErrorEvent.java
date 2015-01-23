/**
 *
 */
package modularity.events.errors;

/**
 * @author Alexander
 *
 */
public class InterruptedErrorEvent extends ErrorEvent {

	/**
	 * @param pExc
	 */
	public InterruptedErrorEvent(final InterruptedException pExc) {
		super(pExc);
	}

	@Override
	public InterruptedException getException() {
		return (InterruptedException) super.getException();
	}

}
