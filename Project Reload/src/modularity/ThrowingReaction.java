/**
 *
 */
package modularity;

import modularity.events.Event;

/**
 * This interface is tied to the Event-class. It is used to implement reactions
 * that are called when a new event fires.
 *
 * @author Alexander
 *
 */
public interface ThrowingReaction {

	/**
	 * This method is called when the event fires.
	 *
	 * @param pThis
	 *            the event that called the method
	 * @throws Exception
	 */
	void react(Event pThis) throws Exception;
}
