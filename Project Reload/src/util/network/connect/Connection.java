/**
 *
 */
package util.network.connect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

import modularity.events.compression.NeedCompressionEvent;
import modularity.events.compression.NeedDecompressionEvent;
import modularity.events.connection.CommandReceivedEvent;
import modularity.events.errors.IOErrorEvent;
import modularity.events.errors.InterruptedErrorEvent;

/**
 * @author Alexander
 *
 */
public class Connection {
	private Thread _disconnectAnnounced = null;
	private Connection _partner;
	private ConcurrentLinkedQueue<String> _receive;
	private ConcurrentLinkedQueue<String> _send;

	private final Socket _sock;

	private int _timeout = 100;

	public Connection() {
		_sock = null;
		_partner = null;
	}

	public Connection(final Connection pPartner) {
		_sock = null;
		_partner = pPartner;
		_send = new ConcurrentLinkedQueue<String>();
		_receive = new ConcurrentLinkedQueue<String>();
		_partner._send = _receive;
		_partner._receive = _send;
	}

	public Connection(final InetSocketAddress pAddress) throws IOException {
		_sock = new Socket();
		_partner = null;
		try {
			_sock.connect(pAddress);
		} catch (final IOException e) {
			new IOErrorEvent(e).run();
		}
	}

	/**
	 * @param pSock
	 *            TODO comment
	 */
	public Connection(final Socket pSock) {
		_sock = pSock;
		_partner = null;
	}

	private void closeConnection(final int pTimeout) {
		assert (pTimeout >= 0);

		if (_disconnectAnnounced == null) {
			_disconnectAnnounced = new Thread() {
				@Override
				public void run() {
					if (pTimeout != 0) {
						if (_sock != null) {
							try {
								wait(_timeout);
							} catch (final InterruptedException e) {
								new InterruptedErrorEvent(e).run();
							}
						}

					}
					if (_sock != null) {
						try {
							_sock.close();
						} catch (final IOException e) {
							new IOErrorEvent(e).run();
						}
					}
					if (_partner != null) {
						_send = null;
						_receive = null;
						_partner = null;
					}
				}
			};
			_disconnectAnnounced.start();
		} else {
			_disconnectAnnounced.notify();
		}
	}

	public synchronized void disconnect() throws IOException, InterruptedException {
		if (_disconnectAnnounced == null) {
			send("x");
			closeConnection(_timeout);
		} else {
			closeConnection(0);
		}
	}

	public synchronized String receive() throws IOException, InterruptedException {
		if (_partner != null) {
			if (_receive.isEmpty()) {
				try {
					wait();
				} catch (final InterruptedException e) {
					new IOErrorEvent(new IOException()).run();
				}
			}
			String s = "";
			while (!_receive.isEmpty()) {
				s = s + _receive.poll();
			}
			new CommandReceivedEvent(s, this).run();
			return s;
		}
		if (_sock != null) {
			BufferedReader in;
			try {
				in = new BufferedReader(new InputStreamReader(
						_sock.getInputStream()));
				try {
					String str = "";
					do {
						final NeedDecompressionEvent ev = new NeedDecompressionEvent(
								in.readLine());
						ev.run();
						ev.waitForCompletion();
						str = str + ev.getMessage() + "\n";
					} while (in.ready());
					new CommandReceivedEvent(str, this).run();
					return str;
				} catch (final IOException e) {
					new IOErrorEvent(e).run();
				}
			} catch (final IOException e1) {
				new IOErrorEvent(e1).run();
			}
		}
		return null;
	}

	public synchronized void send(final String pMessage) {
		if (_partner != null) {
			_send.addAll(pMessage.contains("\n") ? Arrays.asList(pMessage
					.split("\n")) : Arrays.asList(pMessage));
			_partner.notifyAll();
		}
		if (_sock != null) {
			try {
				final PrintWriter out = new PrintWriter(
						_sock.getOutputStream(), true);
				final NeedCompressionEvent ev = new NeedCompressionEvent(
						pMessage);
				ev.run();
				ev.waitForCompletion();
				out.println(ev.getMessage());
				out.flush();
			} catch (final IOException e) {
				new IOErrorEvent(e).run();
			}
		}
	}
}
