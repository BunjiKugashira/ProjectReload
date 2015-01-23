/**
 *
 */
package util.network.connect;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This interface makes using InetSockets the same was as InternalSockets
 * possible.
 *
 * @author Alexander
 *
 */
public interface SocketInterface {
	/**
	 * Closes this socket.
	 * 
	 * @throws IOException
	 * @see java.net.Socket#close()
	 */
	public void close() throws IOException;

	/**
	 * Returns an input stream for this socket.
	 * 
	 * @see java.net.Socket#getInputStream()
	 * @return an input stream for reading bytes from this socket.
	 * @throws IOException
	 */
	public InputStream getInputStream() throws IOException;

	/**
	 * Returns an output stream for this socket.
	 * 
	 * @see java.net.Socket#getOutputStream()
	 * @return an output stream for writing bytes to this socket.
	 * @throws IOException
	 */
	public OutputStream getOutputStream() throws IOException;
}
