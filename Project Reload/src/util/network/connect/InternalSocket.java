/**
 *
 */
package util.network.connect;

import java.io.IOException;
import java.util.IllegalFormatException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import modularity.events.errors.IllegalFormatErrorEvent;
import modularity.events.errors.InterruptedErrorEvent;

/**
 * This class is for creating internal connections. They can be handled like a
 * normal connection but don't have the drawbacks of using ports, etc.
 * 
 * @author Alexander
 *
 */
public class InternalSocket implements SocketInterface {

	/**
	 *
	 * @author Alexander
	 *
	 */
	private class InputStream extends java.io.InputStream {
		private final Queue<Character> _buff;

		InputStream() {

			_buff = new ConcurrentLinkedQueue<Character>();
		}

		/**
		 * Returns the internal buffer to the queue.
		 * 
		 * @return the Buffer
		 */
		public Queue<Character> getBuffer() {
			return _buff;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.io.InputStream#read()
		 */
		@Override
		public int read() throws IOException {
			if (_buff.isEmpty()) {
				try {
					wait();
				} catch (final InterruptedException e) {
					throw new IOException("Error: InternalSocket was interrupted.");
				}
			}
			try {
				return _buff.poll();
			} catch (final IllegalFormatException e) {
				new IllegalFormatErrorEvent(e).run();
				return -1;
			}
		}
		
		@Override
		public int available() {
			return _buff.size();
		}

	}

	/**
	 *
	 * @author Alexander
	 *
	 */
	private class OutputStream extends java.io.OutputStream {
		private final Queue<Character> _buff;
		private final InputStream _in;

		OutputStream(final InputStream pIn) {
			assert (pIn != null);
			_in = pIn;
			_buff = _in.getBuffer();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.io.OutputStream#write(int)
		 */
		@Override
		public void write(final int arg0) {
			_buff.add((char) arg0);
		}
		
		@Override
		public void flush() {
			_in.notifyAll();
		}

	}
	
	private InputStream _in;
	private OutputStream _out;

	/**
	 * Constructor of class InternalSocket. Creates a new, unconnected socket.
	 */
	public InternalSocket() {
		_in = null;
		_out = null;
	}

	/**
	 * Constructor of class InternalSocket. Creates a new socket and connects it
	 * to the given socket.
	 * 
	 * @param pSock
	 *            the socket to connect to.
	 */
	public InternalSocket(final InternalSocket pSock) {
		_in = new InputStream();
		pSock._in = new InputStream();
		_out = new OutputStream(pSock._in);
		pSock._out = new OutputStream(_in);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see util.network.connect.SocketInterface#close()
	 */
	@Override
	public void close() throws IOException {
		// TODO wake up input and make it throw an IOException if it's still waiting
		// TODO wake up output and make it throw an IOException on the other input if it's still waiting
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see util.network.connect.SocketInterface#getInputStream()
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		// TODO Auto-generated method stub
		return _in;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see util.network.connect.SocketInterface#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() throws IOException {
		// TODO Auto-generated method stub
		return _out;
	}

}
