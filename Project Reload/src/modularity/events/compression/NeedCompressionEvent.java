/**
 *
 */
package modularity.events.compression;

import modularity.events.Event;

/**
 * @author Alexander
 *
 */
public class NeedCompressionEvent extends Event<NeedCompressionEvent.Message> {
	/**
	 * @author Alexander
	 *
	 */
	public class Message {
		public String _message;
	}
	/**
	 * @param pEvent
	 */
	public NeedCompressionEvent() {
		super("NeedCompressionEvent");
	}

	/**
	 *
	 */
	public static final NeedCompressionEvent EVENT = new NeedCompressionEvent();
}
