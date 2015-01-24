/**
 *
 */
package modularity.events.errors;

import java.io.IOException;

/**
 * @author Alexander
 *
 */
public class IOErrorEvent extends ErrorEvent {

	/**
	 * @param pExc
	 */
	public IOErrorEvent(final IOException pExc) {
		super(pExc);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() throws IOException {
		try {
			super.run();
		} catch (final Exception e) {
			throw (IOException) e;
		}
	}

}
