/**
 *
 */
package modularity.events.compression;

import modularity.events.Event;

/**
 * @author Alexander
 *
 */
public class NeedDecompressionEvent extends Event<NeedDecompressionEvent.Message> {
	/**
	 * @param pEvent
	 */
	public NeedDecompressionEvent() {
		super("NeedDecompressionEvent");
	}
	public class Message {
		public String _message;
	}
	/**
	 *
	 */
	public static final NeedDecompressionEvent EVENT = new NeedDecompressionEvent();

}
