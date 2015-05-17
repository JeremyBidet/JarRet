package fr.upem.jarret.server;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;




import fr.upem.jarret.server.ServerInformation.ServerState;
import fr.upem.jarret.worker.Job;


/**
 * This is it!
 * 
 * @author Enzo
 * @author Jeremy
 */
public class ServerJarRet {

	private final ServerConfiguration  configuration;
	private final ServerInformation    informations;
	private final Map<String, Command> commands;
	
	private final ExecutorService      thread_pool;
	private final Map<Long, Job>       jobs;
	
	private       ServerSocketChannel  ssc;
	

	
	
	public ServerJarRet() throws IOException {
		this.configuration = new ServerConfiguration("config/JarRetConfig.json");
		this.informations = new ServerInformation();
		this.commands = CommandFactory.create();
		
		this.thread_pool = Executors.newFixedThreadPool(this.configuration.MAX_THREAD_POOL_SIZE);
		this.jobs = new TreeMap<Long, Job>();
		
		this.ssc = ServerSocketChannel.open();
		this.ssc.configureBlocking(false);
		this.ssc.bind(this.configuration.LOCAL_PORT);
		
		
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
		// TODO get job from file, http request, etc... and pass it to Job constructor
		Job j = new Job("");
		if( !this.jobs.containsKey(j.getID()) )
			this.jobs.put(j.getID(), j);
	}
	
	/**
	 * 
	 * @return a copy of the map sorted by job priority
	 */
	public Map<Long, Job> getJobsSortedByPriority() {
		Map<Long, Job> result = new LinkedHashMap<>();
		this.jobs.entrySet().stream()
			.sorted((j1, j2) -> { return j1.getValue().compareTo(j2.getValue()); })
			.forEach(e -> result.put(e.getKey(), e.getValue()));
		return result;
	}
	
	
	/**
	 * @throws IOException 
	 * 
	 */
	public void start() throws IOException {
	
		/* Server has a list of jobs to complete (sorted by job priority).
		 * These job implements Callable<T>, where T is the type of the result, or a simple Runnable task.
		 * Callable<T> is equivalent to give a Runnable task and a T result.
		 * See :
		 * - ExecutorService::submit(Callable<T> task)
		 * - ExecutorService::submit(Runnable task, T result)
		 * - ExecutorService::submit(Runnable task)
		 * - ExecutorService::invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
		 * As this list is sorted by job priority, we can use the peek (or poll to remove) method
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
		while(informations.getServerState() != ServerState.CLOSED ){

			this.thread_pool.execute(()->{
				ByteBuffer bb = ByteBuffer.allocate(this.configuration.MAX_ANSWER_SIZE);
				SocketChannel client = null;
				try {
					client = ssc.accept();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					while( bb.hasRemaining() && client.read(bb) != -1 );
					bb.flip();
					String s_header = Charset.forName("ASCII").decode(bb).toString();
					if(s_header.contains("GET")){
						bb.clear();
						//we have to send the job
					}
					else if(s_header.contains("POST")){
						File file = new File("result.log");
						Charset charset = Charset.forName("UTF-8");
						//write the result in the file
					}
					} catch (Exception e) {
					
					e.printStackTrace();
				}
			});
			
		}
	}
	
	
	


	/**
	 * @throws InterruptedException 
	 * 
	 */
	public void shutdown() throws InterruptedException {
		this.thread_pool.shutdown();
		this.thread_pool.awaitTermination(this.configuration.SHUTDOWN_TIMEOUT, TimeUnit.MILLISECONDS);
		this.informations.setServerState(ServerState.STOPPED);
	}
	
	/**
	 * @throws InterruptedException 
	 * @throws IOException 
	 * 
	 */
	public void close() throws InterruptedException, IOException {
		this.thread_pool.shutdownNow();
		this.thread_pool.awaitTermination(this.configuration.SHUTDOWN_TIMEOUT, TimeUnit.MILLISECONDS);
		ssc.close();
		this.informations.setServerState(ServerState.CLOSED);
		
	}
	
	
	public static void main(String[] args) throws IOException {
		ServerJarRet sj = new ServerJarRet();
		sj.start();
	}

}
