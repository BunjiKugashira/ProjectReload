/**
 *
 */
package util.meta;

import java.util.concurrent.TimeoutException;

/**
 * @author Alexander
 *
 */
public interface Loadable {
	/**
	 * @throws TimeoutException
	 * @throws DeadlockException
	 *
	 */
	public void load() throws DeadlockException, TimeoutException;
}
