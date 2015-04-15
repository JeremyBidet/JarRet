package fr.upem.jarret.server;

import java.util.HashMap;
import java.util.Map;

import fr.upem.jarret.server.ServerInformation.ServerState;


/**
 * @author Jeremy
 */
public class CommandFactory {
	
	public static Map<String, Command> create() {
		Map<String, Command> commands = new HashMap<String, Command>();
		
		commands.put("SHUTDOWN", (server) -> {
			try {
				server.shutdown();
			} catch( Exception e ) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			server.getInformations().setServerState(ServerState.STOPPED);
			return server.getInformations();
		});
		
		commands.put("SHUTDOWN NOW", (server) -> {
			try {
				server.close();
			} catch( Exception e ) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			server.getInformations().setServerState(ServerState.CLOSED);
			return server.getInformations();
		});
		
		commands.put("INFO", (server) -> {
			return server.getInformations();
		});
		
		return commands;
	}
	
	public static void enhance(Map<String, Command> commands, String command_name, Command command) {
		commands.put(command_name, command);
	}
	
}
