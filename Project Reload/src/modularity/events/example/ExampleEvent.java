/**
 *
 */
package modularity.events.example;

import modularity.events.Event;

/**
 * @author Alexander
 *
 */
public class ExampleEvent extends Event<Object> {
	/**
	 * @param pEvent
	 */
	private ExampleEvent() {
		super("ExampleEvent");
	}

	/**
	 *
	 */
	public static final ExampleEvent EVENT = new ExampleEvent();
}
