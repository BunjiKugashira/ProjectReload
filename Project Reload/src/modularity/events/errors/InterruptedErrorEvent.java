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

	@Override
	public void run() throws InterruptedException {
		try {
			super.run();
		} catch (final Exception e) {
			throw (InterruptedException) e;
		}
	}

}
