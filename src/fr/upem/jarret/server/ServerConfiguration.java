package fr.upem.jarret.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * @author Enzo
 * @author Jeremy
 */
public class ServerConfiguration {
	
	public final TimeUnit		TIMEOUT_TIMEUNIT = TimeUnit.MILLISECONDS;

	public final SocketAddress	LOCAL_PORT;
	public final String			ANSWER_PATH;
	public final String			LOG_PATH;
	public final int			MAX_ANSWER_SIZE;
	public final int			MAX_THREAD_POOL_SIZE;
	public final long			CONNECT_TIMEOUT;
	public final long			ACCEPT_TIMEOUT;
	public final long			COMPUTE_TIMEOUT;
	public final long			SHUTDOWN_TIMEOUT;
	
	

	public ServerConfiguration(String config) {
		HashMap<String, Object> map = null;
		try {
			map = new ObjectMapper().readValue(config, new TypeReference<HashMap<String, Object>>() {});
		} catch(JsonParseException e) {
			e.printStackTrace();
		} catch(JsonMappingException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		this.LOCAL_PORT				= new InetSocketAddress((int) map.get("LocalPort"));
		this.ANSWER_PATH			= (String) map.get("AnswerPath");
		this.LOG_PATH				= (String) map.get("LogPath");
		this.MAX_ANSWER_SIZE		= (int) map.get("MaxAnswerSize");
		this.MAX_THREAD_POOL_SIZE	= (int) map.get("MaxThread");
		this.CONNECT_TIMEOUT		= (long) map.get("ConnectTimeout");
		this.ACCEPT_TIMEOUT			= (long) map.get("AcceptTimeout");
		this.COMPUTE_TIMEOUT		= (long) map.get("ComputeTimeout");
		this.SHUTDOWN_TIMEOUT		= (long) map.get("ShutdownTimeout");
		
		
	}


	
	
	
	
}
