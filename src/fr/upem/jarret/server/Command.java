package fr.upem.jarret.server;

import java.util.function.Function;


/**
 * Define server commands.
 * 
 * @author Jeremy
 * @param <T>
 */
@FunctionalInterface
public interface Command extends Function<ServerJarRet, ServerInformation> {

	@Override
	public ServerInformation apply(ServerJarRet server);
	
}
