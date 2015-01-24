/**
 *
 */
package modularity;

import modularity.events.Event;

/**
 * This interface is tied to the Event-class. It is used to implement reactions
 * that are called when a new event fires.
 *
 * @author Alexander Otto
 */
public interface Reaction extends ThrowingReaction {
	@Override
	void react(Event pThis);

}
