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
	private String _message;
	/**
	 * @param pMessage 
	 * 
	 */
	public NeedCompressionEvent(String pMessage) {
		super();
		setMessage(pMessage);
	}
	/**
	 * @return the _message
	 */
	public String getMessage() {
		return _message;
	}
	/**
	 * @param pMessage the _message to set
	 */
	public void setMessage(String pMessage) {
		_message = pMessage;
	}

}
