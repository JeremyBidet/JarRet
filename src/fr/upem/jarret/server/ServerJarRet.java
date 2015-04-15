package fr.upem.jarret.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import fr.upem.jarret.worker.Job;

// TODO class

/**
 * This is
 * 
 * @author Enzo
 * @author Jeremy
 */
public class ServerJarRet {

	private final ServerConfiguration  configuration;
	private final ServerInformation    informations;
	private final Map<String, Command> commands;
	
	private final int                  MAX_THREAD_POOL_SIZE = 16;
	private final long                 SHUTDOWN_TIMEOUT = 3000L;
	private final ExecutorService      thread_pool;
	private final Map<Long, Job>       jobs;
	
	private       ServerSocketChannel  ssc;
	
	private final int                  MAX_ANSWER_SIZE = 4096;
	private final ByteBuffer           bb;
	
	
	public ServerJarRet() throws IOException {
		this.configuration = new ServerConfiguration();
		this.informations = new ServerInformation();
		this.commands = CommandFactory.create();
		
		this.thread_pool = Executors.newFixedThreadPool(MAX_THREAD_POOL_SIZE);
		this.jobs = new TreeMap<Long, Job>(Comparator
				.comparingInt(j1 -> this.jobs.get(j1).getPriority())
				.reversed());
		
		this.ssc = ServerSocketChannel.open();
		this.ssc.configureBlocking(false);
		this.ssc.bind(null);
		
		this.bb = ByteBuffer.allocateDirect(MAX_ANSWER_SIZE);	
	}
	
	
	/**
	 * @return the server configuration
	 * @see ServerConfiguration
	 */
	public ServerConfiguration getConfiguration() {
		return this.configuration;
	}
	
	/**
	 * @return the server informations
	 * @see ServerInformation
	 */
	public ServerInformation getInformations() {
		return this.informations;
	}
	
	/**
	 * @return a set of name of contained commands
	 * @see Command
	 */
	public Set<String> getCommands() {
		return commands.keySet();
	}
	
	/**
	 * Execute the following command and, then, return informations about this server.<br>
	 * @return if command exist, the {@linkplain ServerInformation server informations}, otherwise null.
	 * @see Command
	 */
	public ServerInformation execute(String command) {
		return this.commands.containsKey(command.toUpperCase()) ? this.commands.get(command.toUpperCase()).apply(this) : null;
	}
	
	/**
	 * Add a command to the commands map.<br>
	 * @param command_name the command name
	 * @param command the code or function to execute
	 */
	public void enhance(String command_name, Command command) {
		this.commands.put(command_name, command);
	}
	
	/**
	 * Parse new incoming jobs as JSON files and add them, if job ID does not already exist.
	 */
	public void updateJobs() {
		// TODO
		Job j = new Job("");
		if( !this.jobs.containsKey(j.getID()) )
			this.jobs.put(j.getID(), j);
	}
	
	/**
	 * 
	 * @return a copy of the map sorted by job priority
	 */
	public Map<Long, Job> getJobsSortedByValue() {
		Map<Long, Job> result = new LinkedHashMap<>();
		this.jobs.entrySet().stream()
			.sorted((j1, j2) -> { return j1.getValue().compareTo(j2.getValue()); })
			.forEach(e -> result.put(e.getKey(), e.getValue()));
		return result;
	}
	
	
	/**
	 * 
	 */
	public void start() {
		// TODO server start
		/* Server has a list of jobs to complete (sorted by job priority).
		 * These job implements Callable<T>, where T is the type of the result, or a simple Runnable task.
		 * Callable<T> is equivalent to give a Runnable task and a T result.
		 * See :
		 * - ExecutorService::submit(Callable<T> task)
		 * - ExecutorService::submit(Runnable task, T result)
		 * - ExecutorService::submit(Runnable task)
		 * - ExecutorService::invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
		 * As this list is sorted by job priority, we can use the peek (or poll, to remove) method
		 * to get the head (highest priority job) and submit it to the thread pool.
		 * Then a future of the result is returned. A future is a time wrapper for object.
		 * A call to get() will block the thread until result is available.
		 * A call to get(long timeout, TimeUnit unit) will block until result or timeout is reached.
		 * Both throw exceptions :
		 * - Cancellation... when a cancel operation was invoke on this thread
		 * - Execution... when unexpected execution error occurs
		 * - Interrupted... when thread is interrupted
		 * - Timeout... when timeout is reached
		 */
	}

	/**
	 * @throws InterruptedException 
	 * 
	 */
	public void shutdown() throws InterruptedException {
		// TODO stop accepting client connection
		this.thread_pool.shutdown();
		this.thread_pool.awaitTermination(SHUTDOWN_TIMEOUT, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * @throws InterruptedException 
	 * 
	 */
	public void close() throws InterruptedException {
		// TODO stop server, close connection
		this.thread_pool.shutdownNow();
		this.thread_pool.awaitTermination(SHUTDOWN_TIMEOUT, TimeUnit.MILLISECONDS);
		this.bb.clear();
	}
	
	
	public static void main(String[] args) {
		// TODO server main
	}

}
