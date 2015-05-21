/**
 *
 */
package launcher;

import error.Log;
import modularity.ReactionOld;
import modularity.events.EventOld;
import modularity.events.example.ExampleEvent;
import util.meta.FileManager;

/**
 * @author Alexander
 *
 */
public class Launcher {

	/**
	 * 
	 * @param args
	 *            the arguments given at launching.
	 */
	public static void main(final String[] args) throws InterruptedException {
		// new ExampleEventListener();
		FileManager.createDirectory(FileManager.getPath("TestPath01"));
		FileManager.loadFiles(FileManager
				.getPath("bin\\modularity\\eventListener\\example"));
		System.out.println("Registering new Events.");
		for (int i = 0; i < 4; i++) {
			ExampleEvent.container.registerReaction("Reaction " + i,
					new ReactionOld() {
						@Override
						public void react(final EventOld pThis) {
							try {
								Thread.sleep((long) (Math.random() * 5000));
							} catch (final InterruptedException e) {
								Log.logError(e);
								Log.crash();
							}
							System.out.println("Event concluded.");
						}
					});
		}
		System.out.println("Creating new Event.");
		final ExampleEvent ev = new ExampleEvent();
		System.out.println("Running Event.");
		ev.run();
		System.out.println("Event is running.");
		ev.waitForCompletion();
		System.out.println("Event finished.");
		// new TimeoutErrorEvent(new TimeoutException("Test")).run();
		
		
		// TODO Load all EventListeners
		// TODO Open up a gui
		
		// TODO Patch
		// TODO Wait for user-input
		// TODO Log in
		// TODO Start internal Server
		// TODO Wait for user-input
		
		// TODO Start Client
	}

}
