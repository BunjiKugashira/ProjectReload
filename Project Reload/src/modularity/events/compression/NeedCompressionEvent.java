/**
 *
 */
package modularity.events.compression;

import modularity.events.NonThrowingEvent;

/**
 * @author Alexander
 *
 */
public class NeedCompressionEvent extends NonThrowingEvent {
	/**
	 *
	 */
	public static final EventContainer container = new EventContainer();
	private String _message;

	/**
	 * @param pEv
	 * @param pMessage
	 *
	 */
	public NeedCompressionEvent(final EventContainer pEv, final String pMessage) {
		super(pEv);
		setMessage(pMessage);
	}

	/**
	 * @param pMessage
	 *
	 */
	public NeedCompressionEvent(final String pMessage) {
		super(container);
		setMessage(pMessage);
	}

	/**
	 * @return the _message
	 */
	public String getMessage() {
		return _message;
	}

	/**
	 * @param pMessage
	 *            the _message to set
	 */
	public void setMessage(final String pMessage) {
		_message = pMessage;
	}

}
