/**
 *
 */
package launcher;

import java.util.concurrent.TimeoutException;

import modularity.Reaction;
import modularity.events.Event;
import modularity.events.NonThrowingEvent;
import modularity.events.errors.TimeoutErrorEvent;

/**
 * @author Alexander
 *
 */
public class Launcher {

	/**
	 * @param args
	 *            the arguments given at launching.
	 */
	public static void main(final String[] args) {
		System.out.println("Registering new Events.");
		for (int i = 0; i < 4; i++) {
			NonThrowingEvent.container.registerReaction("Reaction " + i,
					new Reaction() {
				@Override
				public void react(final Event pThis) {
					try {
						Thread.sleep((long) (Math.random() * 5000));
					} catch (final InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("Event concluded.");
				}
			});
		}
		System.out.println("Creating new Event.");
		final NonThrowingEvent ev = new NonThrowingEvent();
		System.out.println("Running Event.");
		ev.run();
		System.out.println("Event is running.");
		try {
			ev.waitForCompletion();
		} catch (final InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Event finished.");
		new TimeoutErrorEvent(new TimeoutException("Test")).run();
	}

}
