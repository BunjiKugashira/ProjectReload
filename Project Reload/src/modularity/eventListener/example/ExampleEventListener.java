/**
 *
 */
package modularity.eventListener.example;

import java.util.concurrent.TimeoutException;

import modularity.events.example.ExampleEvent;
import util.meta.DeadlockException;
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
	public void load() throws DeadlockException, TimeoutException {
		// Register Reactions like this:
		ExampleEvent.EVENT.registerReaction.start("Example Reaction",
				ExampleEvent.EVENT.new Reaction() {
					@Override
					public void react(Object pArgs) throws Exception {
						// Write the Reaction's code here.
					}
				});
	}

}
