/**
 *
 */
package modularity;

import modularity.events.Event;

/**
 * @author Alexander
 *
 */
public interface ThrowingReaction {

	/**
	 * This method is called when the event fires.
	 *
	 * @param ev
	 *            the event that called the method
	 * @throws Exception
	 */
	void react(Event ev) throws Exception;
}
