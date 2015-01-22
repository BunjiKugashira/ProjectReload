/**
 * 
 */
package modularity;

/**
 * @author Alexander
 *
 * This interface is tied to the Event-class. It is used to implement reactions that are called when a new event fires.
 */
public interface Reaction {

	/**
	 * This method is called when the event fires.
	 * 
	 * @param pThis the event that called the method
	 */
	void react(Event pThis);

}
