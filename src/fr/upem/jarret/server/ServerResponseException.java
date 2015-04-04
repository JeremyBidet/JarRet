package fr.upem.jarret.server;


/**
 * This Exception is threw when the server response has invalid fields or format,
 * or when server send a timeout request to the client.
 * 
 * @author Jeremy
 */
public class ServerResponseException extends Exception {

	private static final long serialVersionUID = -407873252765720278L;
	
	/**
	 * This throwable handle a timeout given by the server in case of there are no tasks to compute. 
	 * @author Jeremy
	 */
	public static class ServerResponseThrowable extends Throwable {
		private static final long serialVersionUID = 4622875205638935676L;
		private final long timeout;
		
		public ServerResponseThrowable(long l) {
			super("REQUEST Timeout");
			this.timeout = l;
		}
		
		public long getTimeout() {
			return timeout;
		}
	}
	
	public ServerResponseException() {
		super();
	}
	
	public ServerResponseException(String message) {
		super(message);
	}
	
	public ServerResponseException(String message, long l) {
		super(message, new ServerResponseThrowable(l));
	}
	
	
}
