/**
 * 
 */
package util.network.connect;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentLinkedQueue;
import util.meta.ManagedThread;

/**
 * @author Alexander
 *
 */
public class StringConnection implements Connection {
	public class Address implements Connection.Address {
		
	}
	
	private static Hashtable<Address, StringConnection> _accepters = new Hashtable<Address, StringConnection>();
	public final Address ADDRESS;
	private ConcurrentLinkedQueue<String> _receive;
	private ConcurrentLinkedQueue<String> _send;
	private StringConnection _partner;
	private ManagedThread _waiter;
	private boolean _isConnected;
	private Hashtable<String, ManagedThread> _waitForMessage;
	private ManagedThread _disconnect;
	/**
	 * 
	 */
	public StringConnection() {
		ADDRESS = new Address();
		_receive = new ConcurrentLinkedQueue<String>();
		_send = new ConcurrentLinkedQueue<String>();
		_partner = null;
		_isConnected = false;
		_waitForMessage = new Hashtable<String, ManagedThread>();
	}
	
	private StringConnection(StringConnection pPartner) {
		assert(pPartner != null);
		assert(pPartner._isConnected == false);
		ADDRESS = new Address();
		_partner = pPartner;
		_send = _partner._receive;
		_receive = _partner._send;
		_isConnected = true;
		_partner._isConnected = true;
		_waitForMessage = new Hashtable<String, ManagedThread>();
	}
	
	private void wakeUp() {
		_waiter.wakeUp();
	}
	
	private boolean waitForMessage(String pMessage, int pTimeout) throws InterruptedException {
		_waitForMessage.put(pMessage, ManagedThread.currentThread());
		try {
			ManagedThread.sleep(pTimeout);
		} catch (InterruptedException e) {
			throw(e);
		}
		if (_waitForMessage.containsKey(pMessage)) {
			_waitForMessage.remove(pMessage);
			return false;
		}
		else {
			return true;
		}
	}

	/* (non-Javadoc)
	 * @see util.network.connect.Connection#send(java.lang.String)
	 */
	@Override
	public void send(String pMessage) {
		_send.add(pMessage);
		_partner.wakeUp();
	}

	/* (non-Javadoc)
	 * @see util.network.connect.Connection#receive(int)
	 */
	@Override
	public String receive(int pTimeout) throws InterruptedException {
		Instant start = Instant.now();
		while (_receive.isEmpty() && (pTimeout == -1 || start.until(Instant.now(), ChronoUnit.MILLIS) < pTimeout)) {
			_waiter = ManagedThread.currentThread();
			try {
				ManagedThread.sleep(pTimeout);
			} catch (InterruptedException e) {
				throw(e);
			}
		}
		return _receive.poll();
	}

	/* (non-Javadoc)
	 * @see util.network.connect.Connection#receive()
	 */
	@Override
	public String receive() throws InterruptedException {
		return receive(-1);
	}

	/* (non-Javadoc)
	 * @see util.network.connect.Connection#accept()
	 */
	public void accept() throws InterruptedException {
		accept(-1);
	}

	/* (non-Javadoc)
	 * @see util.network.connect.Connection#accept(int)
	 */
	public void accept(int pTimeout) throws InterruptedException {
		_accepters.put(ADDRESS, this);
		try {
			ManagedThread.sleep(pTimeout);
		} catch (InterruptedException e) {
			throw(e);
		}
	}

	/* (non-Javadoc)
	 * @see util.network.connect.Connection#connect(util.network.connect.Connection.Address)
	 */
	@Override
	public void connect(Connection.Address pTarget) {
		connect(pTarget, -1);
	}

	/* (non-Javadoc)
	 * @see util.network.connect.Connection#connect(util.network.connect.Connection.Address, int)
	 */
	@Override
	public void connect(Connection.Address pTarget, int pTimeout) {
		if (_accepters.containsKey(pTarget)) {
			StringConnection con = _accepters.get(pTarget);
			_accepters.remove(pTarget);
			_isConnected = true;
			con._partner = this;
			con._receive = _send;
			con._send = _receive;
			con._isConnected = true;
			con.wakeUp();
		}
		else {
			throw new IllegalArgumentException();
		}
	}

	/* (non-Javadoc)
	 * @see util.network.connect.Connection#disconnect()
	 */
	@Override
	public void disconnect() throws InterruptedException {
		disconnect(-1);
	}

	/* (non-Javadoc)
	 * @see util.network.connect.Connection#disconnect(int)
	 */
	@Override
	public synchronized void disconnect(int pTimeout) throws InterruptedException {
		if (_disconnect == null) {
			_disconnect = ManagedThread.currentThread();
			send(Parser.DISCONNECT);
			try {
				ManagedThread.sleep(pTimeout);
			} catch (InterruptedException e) {
				throw(e);
			}
			_disconnect = null;
			_isConnected = false;
			_partner = null;
			_send = new ConcurrentLinkedQueue<String>();
			_receive = new ConcurrentLinkedQueue<String>();
		}
		else {
			_disconnect.wakeUp();
		}
	}

	/* (non-Javadoc)
	 * @see util.network.connect.Connection#isConnected()
	 */
	@Override
	public boolean isConnected() {
		return _isConnected;
	}

}
