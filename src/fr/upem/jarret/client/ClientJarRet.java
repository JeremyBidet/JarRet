package fr.upem.jarret.client;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import fr.upem.jarret.server.ServerResponseContent;
import fr.upem.jarret.server.ServerResponseException;
import fr.upem.jarret.server.ServerResponseException.ServerResponseThrowable;
import fr.upem.jarret.server.ServerResponseHeader;
import fr.upem.jarret.worker.Worker;
import fr.upem.jarret.worker.WorkerFactory;


/**
 * Instanciate a servable client.<br>
 * This client will ask a server for a task to compute, compute this task, and send back the result.<br>
 * If no error occurs, the client restart the process.<br>
 * If server does not have any task to compute, client sleep for a timeout given by the server
 * and request a new task.<br>
 * If there is a task to compute, but an error occurs while computing, the client send a response with
 * an error message to the server.<br>
 * If there is a task to compute and no error occurs while computing, but the result does not match
 * the required format, the client send a response with an error message to the server.<br>
 * @author Enzo
 * @author Jeremy
 */
public class ClientJarRet {
	
	private final Charset ASCII_CHARSET   = Charset.forName("ASCII");
	private final Charset UTF8_CHARSET   = Charset.forName("UTF-8");
	private final int     MAX_BUFFER_SIZE = 4096;
	
	private final String            client_id;
	private final InetSocketAddress server;
	private final SocketChannel     sc;
	
	private StringBuilder response;
	
	public ClientJarRet(String client_id, String hostname, int port) throws IOException {
		this.client_id = client_id;
		this.server = new InetSocketAddress(hostname, 80);
		this.sc = SocketChannel.open();
		this.sc.configureBlocking(true);
	}
	
	/**
	 * Launch the client.<br>
	 * Ask for a task, get the task, compute the task, send answer, and loop.
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public ClientJarRet start() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		try {
			this.sc.connect(server);
			response = new StringBuilder();
			try {
				requestJob();
				getJob();
			} catch(ServerResponseException e) {
				if( e.getCause().getMessage() == "REQUEST Timeout" ) {
					long timeout = ((ServerResponseThrowable) e.getCause()).getTimeout();
					long time = System.currentTimeMillis();
					while( System.currentTimeMillis() - time < timeout ); // wait for timeout milliseconds
					return this.start();
				}
			} catch(ComputeException e) {
				throwErrorMessage(ComputeException.error_messages.get(e.getExceptionID()));
			}
			post();
			return this.start();
		} catch( IOException e ) {
			System.err.println("Unexpected I/O error happened while connecting to server " + server.getHostName() + ":" + server.getPort() + " !");
		} finally {
			try {
				this.sc.close();
			} catch( IOException e ) {
				System.err.println("Unexpected I/O error happened while closing socket channel !");
			}
		}
		return null;
	}

	/**
	 * Ask a task from the server.
	 * @throws IOException
	 */
	private void requestJob() throws IOException {
		String request = 
				"GET Task HTTP/1.1\r\n"
				+ "Host: " + server.getHostName() + "\r\n"
				+ "\r\n";
		this.sc.write(ASCII_CHARSET.encode(request));
	}

	/**
	 * This method get and parse the task request server response,
	 * initialize the response to send to the server and call
	 * {@linkplain ClientJarRet#computeTask(ServerResponseContent) computeTask()} method.<br>
	 * 
	 * @throws ComputeException see {@linkplain ClientJarRet#computeTask(ServerResponseContent) computeTask()} method
	 * @throws IOException
	 * @throws ServerResponseException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private void getJob() throws IOException, ServerResponseException, ClassNotFoundException, IllegalAccessException, InstantiationException, ComputeException {
		/** receiving server response... **/
		// get header response
		ByteBuffer bb = ByteBuffer.allocateDirect(MAX_BUFFER_SIZE);
		while( bb.hasRemaining() && sc.read(bb) != -1 );
		bb.flip();
		String s_header = ASCII_CHARSET.decode(bb).toString();
		// parse header response
		ServerResponseHeader header = new ServerResponseHeader(s_header).valid();
		// get content response
		String _content = Charset.forName(header.getCharset()).decode(bb).toString();
		// parse content response
		ServerResponseContent content = new ServerResponseContent(_content);
		
		/** setting server response... **/
		// init response (header + content) to send to server
		initResponse(content);
		// compute task job
		computeTask(content);
	}

	/**
	 * This method load the worker requested to get the task and compute it,
	 * check statements, compute the task, check the compute result and then
	 * append the result to the response to send to the server.<br>
	 * 
	 * @param content an object holding worker and task informations
	 * 
	 * @throws ComputeException give error code and message when compute failed
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private void computeTask(ServerResponseContent content) throws ClassNotFoundException, IllegalAccessException, InstantiationException, ComputeException, IOException {
		/** getting job **/
		// load the worker class name from worker url
		Worker worker = WorkerFactory.getWorker(content.getWorkerUrl(), content.getWorkerClassname());
		/** checking statements **/
		// get the job with job id
		if( worker.getJobId() != content.getJobID() ) {
			throw new ComputeException("The job ID does not match with the server requested job ID !", -2);
		}
		// get the version number for this worker
		if( worker.getVersion() != content.getWorkerVersion() ) {
			throw new ComputeException("The worker version does not match with the server requested worker version !", -1);
		}
		/** performing task **/
		// compute the task
		String result = worker.compute(content.getTask());
		// check result format : { "Prime" : false, "Facteur" : 2 }
		if( result.length() + 0 > MAX_BUFFER_SIZE) {
			throw new ComputeException("Compute result is too long !", 1);
		}
		if( result == null ) {
			throw new ComputeException("Compute failed !", 2);
		}
		JsonFactory f = new JsonFactory();
		JsonParser p = null;
		try {
			p = f.createParser(result);
		} catch(JsonParseException e) {
			throw new ComputeException("Compute result does not have a valid JSON format !", 3);
		}
		if( p != null ) {
			while( p.hasCurrentToken() ) {
				if( p.nextValue() == JsonToken.START_OBJECT ) {
					throw new ComputeException("Compute result is nested !", 4);
				}
			}
		}
		/** setting response **/
		response.append("\t\"Answer\": ").append(result).append('\n');
	}

	/**
	 * This method send to the server, the response given by computing the task.<br>
	 * @throws IOException
	 */
	private void post() throws IOException {
		response.append('}');
		/** reset the content length field **/
		int position = response.indexOf("Content-Length") + 16; // offset to value
		String final_response = new StringBuilder()
				.append(response.substring(0, position))
				.append(response.substring(response.indexOf("{")).length())
				.append(response.substring(position+1))
				.toString();
		/** send response to server **/
		this.sc.write(UTF8_CHARSET.encode(final_response));
	}
	
	/**
	 * Initialize the header and the immutable content of the response.
	 * @param content the statements of the response (header + content field)
	 * @return
	 */
	private void initResponse(ServerResponseContent content) {
		response.append(
				"POST Answer HTTP/1.1\r\n"
				+ "Host: " + server.getHostName() + "\r\n"
				+ "Content-Type: " + "application/json" + "\r\n"
				+ "Content-Length: " + 0 + "\r\n"
				+ "\r\n"
				+ "{\n"
    				+ "\t\"JobId\": \"" + content.getJobID() + "\",\n"
    				+ "\t\"WorkerVersion\": \"" + content.getWorkerVersion() + "\",\n"
    				+ "\t\"WorkerURL\": \"" + content.getWorkerUrl() + "\",\n"
    				+ "\t\"WorkerClassName\": \"" + content.getWorkerClassname() + "\",\n"
    				+ "\t\"Task\": " + content.getTask() + ",\n"
    				+ "\t\"ClientId\": \"" + this.client_id + "\",\n");
	}

	/**
	 * Append the error message to the response
	 * @param error_message the error message to append
	 */
	private void throwErrorMessage(String error_message) {
		response.append("\t\"Error\": \"").append(error_message).append("\"\n");
	}
	

	public static void main(String[] args) {
		if( args.length != 3) { 
			// TODO method usage();
			return;
		}
		
		for(int i=0; i < 100; i++) {
			new Thread(() -> {
				try {
					ClientJarRet client = new ClientJarRet(args[0], args[1], Integer.parseInt(args[2]));
					client.start();
				} catch( Exception e ) {
					e.printStackTrace();
				}
			});
		}
	}

}
