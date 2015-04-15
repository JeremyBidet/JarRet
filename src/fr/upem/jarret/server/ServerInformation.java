package fr.upem.jarret.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Keep some informations about the server, such as server state, jobs state, number of client connected, etc...
 * @author Jeremy
 */
public class ServerInformation {
	
	/**
	 * The server state.
	 * @author Jeremy
	 */
	public static enum ServerState {
		/**
		 * The server is started and processing job request with clients.
		 */
		RUNNING,
		/**
		 * The server is waiting for client connection
		 */
		WAITING,
		/**
		 * The server does not accept more client connection
		 */
		FULL,
		/**
		 * The server is still opened but does nothing
		 */
		STOPPED,
		/**
		 * The server is closed
		 */
		CLOSED
	};
	
	/**
	 * The job state.
	 * @author Jeremy
	 */
	public static enum JobState {
		/**
		 * This job is not currently processed by the server.
		 */
		QUEUED,
		/**
		 * This job is currently processed by the server.
		 */
		PENDING,
		/**
		 * This job has been stopped but not completed.
		 */
		STOPPED,
		/**
		 * All tasks has been computed.
		 */
		COMPLETED
	};
	
	private ServerState               server_state;
	private int                       number_of_client;
	private final Map<Long, JobState> jobs_state;
	
	
	/**
	 * Set the server informations to default values :<br>
	 * - State : {@linkplain ServerState#RUNNING running}<br>
	 * - # of client : 0
	 */
	public ServerInformation() {
		this.server_state     = ServerState.RUNNING;
		this.number_of_client = 0;
		this.jobs_state        = new HashMap<>();
	}

	
	/**
	 * @return the current server state
	 */
	public ServerState getServerState() {
		return this.server_state;
	}

	/**
	 * @return the current number of client
	 */
	public int getNumberOfClient() {
		return this.number_of_client;
	}

	/**
	 * @return the current job state map
	 */
	public Map<Long, JobState> getAllJobState() {
		return this.jobs_state;
	}
	
	/**
	 * @param job_id the job id
	 * @return the state of this job
	 */
	public JobState getJobState(Long job_id) {
		return this.jobs_state.get(job_id);
	}
	
	/**
	 * Filter the job state map with the state argument filter
	 * and return a {@link Set} of all job IDs matching the state.
	 * @param state the state filter
	 * @return a set of Long of all job IDs
	 */
	public Set<Long> getJobID(JobState state) {
		return this.jobs_state.entrySet().stream()
				.filter(p -> state == p.getValue())
				.map(e -> e.getKey())
				.collect(Collectors.toSet());
	}

	/**
	 * Call {@linkplain ServerInformation#getJobID(JobState state) getJobID(JobState state)}
	 * with {@link JobState#QUEUED} state
	 * @return the job IDs of all queued jobs
	 */
	public Set<Long> getQueuedJobID() {
		return this.getJobID(JobState.QUEUED);
	}
	
	/**
	 * Call {@linkplain ServerInformation#getJobID(JobState state) getJobID(JobState state)}
	 * with {@link JobState#PENDING} state
	 * @return the job IDs of all pending jobs
	 */
	public Set<Long> getPendingJobID() {
		return this.getJobID(JobState.PENDING);
	}
	
	/**
	 * Call {@linkplain ServerInformation#getJobID(JobState state) getJobID(JobState state)}
	 * with {@link JobState#COMPLETED} state
	 * @return the job IDs of all completed jobs
	 */
	public Set<Long> getCompletedJobID() {
		return this.getJobID(JobState.COMPLETED);
	}
	
	/**
	 * Call {@linkplain ServerInformation#getJobID(JobState state) getJobID(JobState state)}
	 * with {@link JobState#STOPPED} state
	 * @return the job IDs of all stopped jobs
	 */
	public Set<Long> getStoppedJobID() {
		return this.getJobID(JobState.STOPPED);
	}
	
	/**
	 * Retrieves and removes all job ID from completed jobs 
	 * @return a set a all job ID removed
	 */
	public Set<Long> pollCompletedJobID() {
		Set<Long> polled = getCompletedJobID();
		this.jobs_state.entrySet().removeAll(polled);
		return polled;
	}

	/**
	 * @param server_state the server state to set
	 */
	public void setServerState(ServerState server_state) {
		this.server_state = server_state;
	}

	/**
	 * @param number_of_client the number of client to set
	 */
	public void setNumberOfClient(int number_of_client) {
		this.number_of_client = number_of_client;
	}
	
	/**
	 * Add or update the state for this job ID.<br>
	 * @param job_id the job ID to add/update
	 * @param state the new state to assign to this job ID
	 * @return the previous job state, or null
	 */
	public JobState setJobState(Long job_id, JobState state) {
		return this.jobs_state.put(job_id, state);
	}
	
}
