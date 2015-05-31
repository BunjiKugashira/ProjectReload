/**
 * 
 */
package error;

/**
 * @author Alexander Otto
 *
 */
public class Log {

	public static synchronized void logError(Exception e) {
		// TODO log errors to file
		e.printStackTrace();
	}
	
	public static void crash() {
		Runtime.getRuntime().exit(1);
	}
}