/**
 *
 */
package modularity.events.connection;

import modularity.events.NonThrowingEvent;
import util.network.connect.Connection;

/**
 * @author Alexander
 *
 */
public class CommandReceivedEvent extends NonThrowingEvent {
	/**
	 *
	 */
	public static final EventContainer container = new EventContainer();
	private final String _command;
	private final Connection _source;

	/**
	 * @param pEv
	 * @param pCommand
	 * @param pSource
	 *
	 */
	protected CommandReceivedEvent(final EventContainer pEv,
			final String pCommand, final Connection pSource) {
		super(pEv);
		_command = pCommand;
		_source = pSource;
	}

	/**
	 * @param pCommand
	 * @param pSource
	 *
	 */
	public CommandReceivedEvent(final String pCommand, final Connection pSource) {
		super(container);
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
