/**
 *
 */
package util.constants;

/**
 * @author Alexander
 *
 */
public final class Constants {
	public static final String EVENT_LISTENER_PATH = Thread.currentThread()
			.getContextClassLoader().getResource(null).getPath()
			+ "/modularity/eventListener/";
}
