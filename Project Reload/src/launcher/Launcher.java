/**
 *
 */
package launcher;

import error.Log;
import modularity.events.example.ExampleEvent;
import util.meta.FileManagerOld;

/**
 * @author Alexander
 *
 */
public class Launcher {

	/**
	 * 
	 * @param args
	 *            the arguments given at launching.
	 */
	public static void main(final String[] args) {
		float ratio = 0.987f;
		float last = ratio;
		int counter = 0;
		for (int i = 0; i < 1000; i++) {
			if (last > ratio) {
				last = last + (0 - ratio);
				System.out.print("0");
			}
			else {
				last = last + (1 - ratio);
				counter++;
				System.out.print("1");
			}
		}
		System.out.println();
		System.out.println("Counted " + counter + " successes.");
	}

}
