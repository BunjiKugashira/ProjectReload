/**
 * 
 */
package util.network.connect;

import java.util.concurrent.TimeoutException;

/**
 * @author Alexander
 *
 */
public interface Connection {
	/**
	 * @author Alexander
	 *
	 */
	public interface Address {
	}
	public void send(String pMessage);
	public String receive(int pTimeout) throws InterruptedException;
	public String receive() throws InterruptedException;
	public void accept() throws InterruptedException;
	public void accept(int pTimeout) throws InterruptedException;
	public void connect(Address pTarget) throws InterruptedException;
	public void connect(Address pTarget, int pTimeout) throws InterruptedException, TimeoutException;
	public void disconnect() throws InterruptedException;
	public void disconnect(int pTimeout) throws InterruptedException;
	public boolean isConnected();
}
