/**
 * 
 */
package modularity.events.connection;

import util.network.connect.Connection;
import modularity.events.NonThrowingEvent;

/**
 * @author Alexander
 *
 */
public class CommandReceivedEvent extends NonThrowingEvent {
	private final String _command;
	private final Connection _source;
	/**
	 * @param pCommand 
	 * @param pSource 
	 * 
	 */
	public CommandReceivedEvent(final String pCommand, final Connection pSource) {
		_command = pCommand;
		_source = pSource;
	}
	/**
	 * @return the _command
	 */
	public String getCommand() {
		return _command;
	}
	/**
	 * @return the _source
	 */
	public Connection getSource() {
		return _source;
	}

}
