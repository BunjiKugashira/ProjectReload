/**
 * 
 */
package util.network.connect;

/**
 * @author Alexander
 *
 */
public class IpConnection implements Connection {

	/**
	 * 
	 */
	public IpConnection() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see util.network.connect.Connection#send(java.lang.String)
	 */
	@Override
	public void send(String pMessage) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see util.network.connect.Connection#receive(int)
	 */
	@Override
	public String receive(int pTimeout) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see util.network.connect.Connection#receive()
	 */
	@Override
	public String receive() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see util.network.connect.Connection#accept()
	 */
	@Override
	public Connection accept() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see util.network.connect.Connection#accept(int)
	 */
	@Override
	public Connection accept(int pTimeout) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see util.network.connect.Connection#connect(util.network.connect.Connection.Address)
	 */
	@Override
	public void connect(Address pTarget) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see util.network.connect.Connection#connect(util.network.connect.Connection.Address, int)
	 */
	@Override
	public void connect(Address pTarget, int pTimeout) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see util.network.connect.Connection#disconnect()
	 */
	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see util.network.connect.Connection#disconnect(int)
	 */
	@Override
	public void disconnect(int pTimeout) {
		// TODO Auto-generated method stub
		
	}

}
