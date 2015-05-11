package fr.upem.jarret.server;

import java.util.HashMap;
import java.util.Map;


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
				System.err.println("An exception has occur while shutdowning the server !");
			}
			return server.getInformations();
		});
		
		commands.put("SHUTDOWN NOW", (server) -> {
			try {
				server.close();
			} catch( Exception e ) {
				System.err.println("An exception has occured while closing the server !");
			}
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
