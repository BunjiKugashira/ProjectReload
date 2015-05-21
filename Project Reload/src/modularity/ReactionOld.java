/**
 *
 */
package modularity;

import modularity.events.EventOld;

/**
 * This interface is tied to the Event-class. It is used to implement reactions
 * that are called when a new event fires.
 *
 * @author Alexander Otto
 */
public interface ReactionOld extends ThrowingReaction {
	@Override
	void react(EventOld pThis);

}
