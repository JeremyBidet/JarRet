package fr.upem.jarret.worker;

import java.io.IOException;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * @author Jeremy
 */
public class Job implements Comparable<Job>{
	
	private final long   id;
	private final long   task_number;
	private final String description;
	private final int    priority;
	private final String worker_version_number;
	private final String worker_url;
	private final String worker_class_name;
	
	
	/**
	 * Parse a JSON string containing all informations about a job.<br>
	 * This string has the following format:<br>
	 * 	{<br>
	 * 		"JobId" : "1",<br>
	 * 		"JobTaskNumber" : "1000",<br>
	 * 		"JobDescription" : "Test job",<br>
	 * 		"JobPriority" : "1",<br>
	 * 		"WorkerVersionNumber" : "1.2",<br>
	 * 		"WorkerURL" : "http://www.test.fr/test1.jar",<br>
	 * 		"WorkerClassName" : "test.test"<br>
	 * 	}<br>
	 * 
	 * @param job_as_json the JSON string
	 */
	public Job(String job_as_json) {
		HashMap<String, Object> map = null;
		try {
			map = new ObjectMapper().readValue(job_as_json, new TypeReference<HashMap<String, Object>>() {});
		} catch(JsonParseException e) {
			e.printStackTrace();
		} catch(JsonMappingException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		this.id                    = Long.parseLong((String) map.get("JobId"));
		this.task_number           = Long.parseLong((String) map.get("JobTaskNumber"));
		this.description           = (String) map.get("JobDescription");
		this.priority              = Integer.parseInt((String) map.get("JobPriority"));
		this.worker_version_number = (String) map.get("WorkerVersionNumber");
		this.worker_url            = (String) map.get("WorkerURL");
		this.worker_class_name     = (String) map.get("WorkerClassName");
	}


	/**
	 * @return the job ID
	 */
	public long getID() {
		return this.id;
	}

	/**
	 * @return the number of job tasks
	 */
	public long getTaskNumber() {
		return this.task_number;
	}

	/**
	 * @return the job description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @return the job priority
	 */
	public int getPriority() {
		return this.priority;
	}

	/**
	 * @return the worker version number
	 */
	public String getWorkerVersionNumber() {
		return this.worker_version_number;
	}

	/**
	 * @return the worker url
	 */
	public String getWorkerURL() {
		return this.worker_url;
	}

	/**
	 * @return the worker class name
	 */
	public String getWorkerClassName() {
		return this.worker_class_name;
	}
	
	@Override
	public String toString() {
		return "" + this.id + " " + this.priority + " " + this.description;
	}
	
	public String toJSONString() {
		// TODO
		return "";
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Job && ((Job) o).id == this.id;
	}
	
	@Override
	public int hashCode() {
		return (int) this.id;
	}
	
	@Override
	public int compareTo(Job j) {
		return (this.priority - j.priority) <= 0 ? 1 : -1;
	}
	
}
