package fr.upem.jarret.client;

import java.io.IOException;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * This class represent the server response content.<br>
 * The server can send two kind of response:<br>
 * - a task to compute<br>
 * - a request timeout caused by lack of task<br>
 * <br>
 * Fields received:
 * <ul>
 * 	<li>Task:</li>
 * 		<ul>
 * 			<li>job ID</li>
 * 			<li>worker version</li>
 * 			<li>worker url</li>
 * 			<li>worker classname</li>
 * 			<li>task number</li>
 * 		</ul>
 * 	<li>Lack:</li>
 * 		<ul>
 * 			<li>timeout</li>
 *		</ul>
 * </ul>
 * 
 * @author Jeremy
 */
public class ServerResponseContent {
	
	private final long   job_id;
	private final String worker_version;
	private final String worker_url;
	private final String worker_classname;
	private final int    task;
	
	/**
	 * Init the server response content.<br>
	 * Parse the content with a regex and set all fields, ignoring header response.<br>
	 * @param content the content to parse
	 * @throws ServerResponseException if server has no task, server send a timeout
	 * @throws IOException 
	 * @throws JsonParseException 
	 */
	public ServerResponseContent(String content) throws ServerResponseException, IOException {
		HashMap<String, Object> map = null;
		try {
			map = new ObjectMapper().readValue(content, new TypeReference<HashMap<String, Object>>() {});
		} catch(JsonParseException e) {
			throw new ServerResponseException("Server response content format is not valid !");
		} catch(JsonMappingException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
		if( map.containsKey("ComeBackInSeconds") ) {
			throw new ServerResponseException(
					"Server has no task to compute for the moment !",
					((Integer) map.get("ComeBackInSeconds")).longValue());
		}
		this.job_id           = Long.parseLong((String) map.get("JobId"));
		this.worker_version   = (String)   map.get("WorkerVersion");
		this.worker_url       = (String)   map.get("WorkerURL");
		this.worker_classname = (String)   map.get("WorkerClassName");
		this.task             = ((Integer) map.get("Task")).intValue();
	}

	/**
	 * @return the job id
	 */
	public long getJobID() {
		return this.job_id;
	}

	/**
	 * @return the worker version
	 */
	public String getWorkerVersion() {
		return this.worker_version;
	}

	/**
	 * @return the worker url
	 */
	public String getWorkerUrl() {
		return this.worker_url;
	}

	/**
	 * @return the worker classname
	 */
	public String getWorkerClassname() {
		return this.worker_classname;
	}

	/**
	 * @return the task number
	 */
	public int getTask() {
		return this.task;
	}
	
}
