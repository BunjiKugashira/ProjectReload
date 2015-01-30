/**
 *
 */
package util.meta;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import modularity.events.errors.FileNotFoundErrorEvent;
import modularity.events.errors.IOErrorEvent;
import modularity.events.errors.SecurityErrorEvent;

/**
 * @author Alexander
 *
 */
public class FileManager {

	/**
	 * @param pPath
	 * @return fda
	 */
	public static boolean createDirectory(final Path pPath) {
		assert (pPath != null);
		if (Files.exists(pPath)) {
			return false;
		}
		try {
			Files.createDirectory(pPath);
			return true;
			// TODO check if the directory is new or already in use
		} catch (final FileAlreadyExistsException e) {
			return false;
		} catch (final IOException e) {
			new IOErrorEvent(e).run();
		} catch (final SecurityException e) {
			new SecurityErrorEvent(e).run();
		}
		return false;
	}

	/**
	 * @param pPath
	 * @return fda
	 */
	public static Stream<Path> getDirectoryContent(final Path pPath) {
		assert (pPath != null);
		if (!Files.isDirectory(pPath)) {
			return null;
		}
		try {
			// DirectoryStream<Path> stream = Files.newDirectoryStream(pPath);
			return Files.list(pPath);
		} catch (final NotDirectoryException e) {
			return null;
		} catch (final IOException e) {
			new IOErrorEvent(e).run();
		} catch (final SecurityException e) {
			new SecurityErrorEvent(e).run();
		}
		return null;
	}

	/**
	 * @param pPath
	 * @return da
	 */
	public static Path getPath(final String pPath) {
		assert (pPath != null);
		final File dir = new File(pPath);
		assert (dir != null);

		final String path = dir.getAbsolutePath();
		System.out.println(path); // TODO remove

		return dir.toPath();
	}

	/**
	 * @param pPath
	 */
	public static void loadFiles(final Path pPath) {
		assert (pPath != null);
		System.out.println("Path: " + pPath);
		if (Files.isDirectory(pPath)) {
			for (final Path p : (Iterable<Path>) getDirectoryContent(pPath)::iterator) {
				if (Files.isDirectory(p)) {
					loadFiles(p);
				} else if (p.toString().endsWith(".jar")
						|| pPath.toString().endsWith(".zip")) {
					loadJarContent(p);
				} else {
					System.out.println("File: " + p);
					// TODO load p
					if (p.toString().endsWith(".class")) {
						System.out.println("Class found: " + p);
						final ClassLoader cl = ClassLoader
								.getSystemClassLoader();
						String path = p.toString();
						path = path.substring("bin\\".length(), path.length()
								- ".class".length());
						path = path.replace("\\", ".");
						System.out.println(path);
						Class<?> c = null;
						;
						try {
							c = cl.loadClass(path);
							System.out.println(c.getName());
						} catch (final ClassNotFoundException e) {
							e.printStackTrace();
						}
						if (c != null) {
							boolean impl = false;
							for (final Class<?> inf : c.getInterfaces()) {
								try {
									if (inf.equals(Class
											.forName("util.meta.Loadable"))) {
										impl = true;
									}
								} catch (final ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							if (impl) {
								try {
									final Loadable l = (Loadable) c
											.newInstance();
									l.load();
								} catch (final InstantiationException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (final IllegalAccessException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
		} else {
			System.out.println("Error");
			new FileNotFoundErrorEvent(new FileNotFoundException(
					pPath.toString())).run();
		}
	}

	private static void loadJarContent(final JarEntry pJar) {
		System.out.println("JarEntry: " + pJar.getName());
	}

	private static void loadJarContent(final JarFile pJar) {
		final Enumeration<JarEntry> jarEntries = pJar.entries();
		while (jarEntries.hasMoreElements()) {
			loadJarContent(jarEntries.nextElement());
		}
	}

	/**
	 * @param pPath
	 */
	public static void loadJarContent(final Path pPath) {
		assert (pPath != null);
		assert (pPath.toString().endsWith(".jar") || pPath.toString().endsWith(
				".zip"));
		try {
			final JarFile jFile = new JarFile(pPath.toString());
			loadJarContent(jFile);
		} catch (final IOException e) {
			new IOErrorEvent(e).run();
		}
	}

	/**
	 *
	 */
	public FileManager() {
		// TODO Auto-generated constructor stub
	}

}
