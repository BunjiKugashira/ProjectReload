/**
 *
 */
package modularity.events;

import modularity.Reaction;
import modularity.ThrowingReaction;

/**
 * @author Alexander
 *
 */
public class NonThrowingEvent extends Event {
	/**
	 * @author Alexander
	 *
	 */
	public static class EventContainer extends Event.EventContainer {
		/**
		 * @param pKey
		 * @param pReact
		 * @return dfsa
		 */
		public synchronized boolean registerReaction(final String pKey,
				final Reaction pReact) {
			return super.registerReaction(pKey, pReact);
		}

		/**
		 * @param pKey
		 * @param pReact
		 * @param pOrder
		 * @return dfsa
		 */
		public synchronized boolean registerReaction(final String pKey,
				final Reaction pReact, final int pOrder) {
			return super.registerReaction(pKey, pReact, pOrder);
		}

		@Deprecated
		@Override
		public synchronized boolean registerReaction(final String pKey,
				final ThrowingReaction pReact) {
			return super.registerReaction(pKey, pReact);
		}

		@Deprecated
		@Override
		public synchronized boolean registerReaction(final String pKey,
				final ThrowingReaction pReact, final int pOrder) {
			return super.registerReaction(pKey, pReact, pOrder);
		}
	}

	/**
	 *
	 */
	public static final EventContainer container = new EventContainer();

	/**
	 *
	 */
	public NonThrowingEvent() {
		super(container);
	}

	/**
	 * @see Event#Event(EventContainer)
	 * @param pEv
	 */
	protected NonThrowingEvent(final EventContainer pEv) {
		super(pEv);
	}
}