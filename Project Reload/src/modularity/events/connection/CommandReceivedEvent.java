/**
 *
 */
package modularity.events.connection;

import modularity.events.Event;
import util.network.connect.Connection;

/**
 * @author Alexander
 *
 */
public class CommandReceivedEvent extends Event<CommandReceivedEvent.Command> {
	/**
	 * @param pEvent
	 */
	public CommandReceivedEvent() {
		super("CommandReceivedEvent");
	}
	public class Command {
		public String _command;
		public Connection _source;
	}
	/**
	 *
	 */
	public static final CommandReceivedEvent EVENT = new CommandReceivedEvent();
}
