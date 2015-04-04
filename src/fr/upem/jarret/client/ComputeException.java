package fr.upem.jarret.client;

import java.util.HashMap;
import java.util.Map;


/**
 * This Exception is threw when task compute failed.<br>
 * It set an exception ID which matches an error message to reply to the server. 
 * 
 * @author Jeremy
 */
public class ComputeException extends Exception {

	private static final long serialVersionUID = 1510634577771196575L;
	public static final Map<Integer, String> error_messages = new HashMap<>();
	static {
		error_messages.put(-2, "");
		error_messages.put(-1, "");
		
		error_messages.put(1, "Too Long");
		error_messages.put(2, "Computation error");
		error_messages.put(3, "Answer is not valid JSON");
		error_messages.put(4, "Answer is nested");
	}
	
	private final int exception_id;
	
	public ComputeException() {
		super();
		exception_id = 0;
	}
	
	public ComputeException(String message, int exception_id) {
		super(message);
		this.exception_id = exception_id;
	}
	
	public int getExceptionID() {
		return this.exception_id;
	}
	
}
