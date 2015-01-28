/**
 * 
 */
package modularity.eventListener.example;

import modularity.Reaction;
import modularity.events.Event;
import modularity.events.example.ExampleEvent;

/**
 * @author Alexander
 *
 */
public class ExampleEventListener {
	@SuppressWarnings("unused")
	private static ExampleEventListener init = new ExampleEventListener();
	/**
	 * 
	 */
	public ExampleEventListener() {
		ExampleEvent.container.registerReaction("example-reaction", new Reaction() {

			@Override
			public void react(Event pThis) {
				System.out.println("This is an example-reaction.");
			}
		});
	}

}
