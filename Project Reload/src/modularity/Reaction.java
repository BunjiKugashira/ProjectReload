/**
 * 
 */
package modularity;

/**
 * @author Alexander Otto
 *
 */
public abstract class Reaction {
	
	public abstract void react(Object... pObj);
	
	public int getPriority() {
		return 0;
	}
}
