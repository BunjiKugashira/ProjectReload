/**
 *
 */
package modularity.eventListener.example;

import modularity.Reaction;
import modularity.events.Event;
import modularity.events.example.ExampleEvent;
import util.meta.Loadable;

/**
 * @author Alexander
 *
 */
public class ExampleEventListener implements Loadable {
	/**
	 *
	 */
	public ExampleEventListener() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see util.meta.Loadable#load()
	 */
	@Override
	public void load() {
		ExampleEvent.container.registerReaction("example-reaction",
				new Reaction() {

					@Override
					public void react(final Event pThis) {
						System.out.println("This is an example-reaction.");
					}
				});
	}

}
