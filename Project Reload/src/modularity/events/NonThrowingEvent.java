/**
 *
 */
package modularity.events;

/**
 * @author Alexander
 *
 */
public abstract class NonThrowingEvent extends Event {
	@Override
	public void run() {
		try {
			super.run();
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
