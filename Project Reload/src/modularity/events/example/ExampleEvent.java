/**
 *
 */
package modularity.events.example;

import modularity.events.NonThrowingEvent;

/**
 * @author Alexander
 *
 */
public class ExampleEvent extends NonThrowingEvent {
	/**
	 *
	 */
	public static final EventContainer container = new EventContainer();

	/**
	 *
	 */
	public ExampleEvent() {
		super(container);
		_joinTimer = 5000; // Set this to what you think is appropriate or just
							// leave it away.
	}

	/**
	 * @param pEv
	 */
	protected ExampleEvent(final EventContainer pEv) {
		super(pEv);
		_joinTimer = 5000; // see ExampleEvent()
	}

}
