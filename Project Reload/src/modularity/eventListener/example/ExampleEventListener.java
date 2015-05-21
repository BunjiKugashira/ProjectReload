/**
 *
 */
package modularity.eventListener.example;

import modularity.ReactionOld;
import modularity.events.EventOld;
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
				new ReactionOld() {

					@Override
					public void react(final EventOld pThis) {
						System.out.println("This is an example-reaction.");
					}
				});
	}

}
