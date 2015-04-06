package fr.upem.client;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import fr.upem.jarret.client.ComputeException;


/**
 * 
 * @author Jeremy
 */
public class ClientJarRetTest {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	public String ok_result = "{ \"Prime\": \"OK\", \"Result\": 100 }";
	public String not_json_result = "HTTP\"Prime\": \"OK\", \"Result\": 100";
	public String nested_result = "{ \"Prime\": \"OK\", \"Result\": { \"Second\": 100 } }";
	public StringBuilder response_to_server = new StringBuilder().append(
			"POST Answer HTTP/1.1\r\n"
			+ "Host: "           + "http://www.google.com" + "\r\n"
			+ "Content-Type: "   + "application/json"      + "\r\n"
			+ "Content-Length: " + 0                       + "\r\n"
			+ "\r\n"
			+ "\n{\n"
				+ "\t\"JobId\": \""           + 123                            + "\",\n"
				+ "\t\"WorkerVersion\": \""   + "1.1.3"                        + "\",\n"
				+ "\t\"WorkerURL\": \""       + "http://www.google.com/worker" + "\",\n"
				+ "\t\"WorkerClassName\": \"" + "MiningWorker"                 + "\",\n"
				+ "\t\"Task\": "              + 666                            + ",\n"
				+ "\t\"ClientId\": \""        + "First google coin miner"      + "\",\n");
	
	public boolean checkJSON(String result) throws IOException, ComputeException {
		JsonFactory f = new JsonFactory();
		JsonParser p = null;
		p = f.createParser(result);
		try {
			p.nextToken();
		} catch(JsonParseException e) {
			throw new ComputeException("Compute result does not have a valid JSON format !", 3);
		}
		while( p.hasCurrentToken() ) {
			if( p.nextValue() == JsonToken.START_OBJECT ) {
				throw new ComputeException("Compute result is nested !", 4);
			}
		}
		return true;
	}
	
	@Test
	public void computeTaskWithValidResult() throws IOException, ComputeException {		
		assertTrue(checkJSON(ok_result));
	}
	
	@Test
	public void computeTaskWithInvalidResult() throws IOException, ComputeException {
		exception.expect(ComputeException.class);
	    exception.expectMessage("Compute result does not have a valid JSON format !");
		checkJSON(not_json_result);
	}
	
	@Test
	public void computeTaskWithNestedResult() throws IOException, ComputeException {
		exception.expect(ComputeException.class);
	    exception.expectMessage("Compute result is nested !");
		checkJSON(nested_result);
	}
	
	@Test
	public void validateFinalResponse() {
		response_to_server.append("\t\"Answer\": ").append(ok_result).append('\n');
		this.response_to_server.append('}');
		/** reset the content length field **/
		int position = this.response_to_server.indexOf("Content-Length") + 16; // offset to value
		String final_response = new StringBuilder()
				.append(this.response_to_server.substring(0, position))
				.append(this.response_to_server.substring(this.response_to_server.lastIndexOf("\r\n")+2).length())
				.append(this.response_to_server.substring(position+1))
				.toString();
		
		String expected = 
				"POST Answer HTTP/1.1\r\n"
				+ "Host: http://www.google.com\r\n"
				+ "Content-Type: application/json\r\n"
				+ "Content-Length: 228\r\n"
				+ "\r\n"
				+ "\n{\n"
					+ "\t\"JobId\": \"123\",\n"
					+ "\t\"WorkerVersion\": \"1.1.3\",\n"
					+ "\t\"WorkerURL\": \"http://www.google.com/worker\",\n"
					+ "\t\"WorkerClassName\": \"MiningWorker\",\n"
					+ "\t\"Task\": 666,\n"
					+ "\t\"ClientId\": \"First google coin miner\",\n"
					+ "\t\"Answer\": { \"Prime\": \"OK\", \"Result\": 100 }\n"
				+ "}";
		
		assertEquals(final_response, expected);
	}
	
}
