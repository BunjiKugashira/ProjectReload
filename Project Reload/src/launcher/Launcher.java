/**
 *
 */
package launcher;

import modularity.Reaction;
import modularity.events.Event;
import modularity.events.compression.NeedCompressionEvent;

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
		for (int i = 0; i < 150; i++) {
			NeedCompressionEvent.registerReaction("Reaction " + i, new Reaction() {

				@Override
				public void react(Event pThis) {
					try {
						wait((this.hashCode() * 1111) % 5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("Event concluded.");
				}});
		}
		
		NeedCompressionEvent ev = new NeedCompressionEvent("Testmessage");
		ev.run();
	}

}
