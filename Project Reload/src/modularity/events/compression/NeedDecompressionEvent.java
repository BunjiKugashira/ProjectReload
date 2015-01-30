/**
 *
 */
package modularity.events.compression;

/**
 * @author Alexander
 *
 */
public class NeedDecompressionEvent extends NeedCompressionEvent {
	/**
	 *
	 */
	public static final EventContainer container = new EventContainer();

	/**
	 * @param pEv
	 * @param pMessage
	 */
	protected NeedDecompressionEvent(final EventContainer pEv,
			final String pMessage) {
		super(pEv, pMessage);
	}

	/**
	 * @param pMessage
	 */
	public NeedDecompressionEvent(final String pMessage) {
		super(container, pMessage);
	}

}
