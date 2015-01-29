/**
 * 
 */
package modularity.eventListener;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import util.constants.Constants;

/**
 * @author Alexander
 *
 */
public class RegisterEventListener {

	/**
	 * 
	 */
	public static void run() {
		// TODO register Events
		File classDir = new File("/users/myproject/classes/");
		assert(classDir != null);
		
		try {
			URL[] url = { classDir.toURI().toURL() };
			URLClassLoader urlLoader = new URLClassLoader(url);

			String filename;
			for (File file : classDir.listFiles()) {
			    filename = file.getName();
			    if (!filename.endsWith(".class"))
			        continue;

			try {
				Object instance = urlLoader
				    .loadClass("org.mypackage." + filename)
				    .getConstructor()
				    .newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		} catch(MalformedURLException e) {
			e.printStackTrace();
		}
	}

}
