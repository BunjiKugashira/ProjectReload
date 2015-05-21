/**
 * 
 */
package util.meta;

/**
 * @author Alexander Otto
 *
 */
public class ManagedThread extends Thread {
	protected final String _identifier = "";
	
	public void start() {
		start(0);
	}
	
	public void start(int pPriority) {
		// TODO make a priority-queue
		super.start();
	}
	
	public String getIdentifier() {
		return _identifier;
	}

}
