package fr.upem.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import fr.upem.jarret.server.ServerResponseException;
import fr.upem.jarret.server.ServerResponseHeader;


/**
 * @author Jeremy
 */
public class ServerResponseHeaderTest {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();

	
	private String httpVersions() {
		final List<String>  HTTP_VERSIONS = Arrays.asList("1.0", "1.1", "1.2");
		StringBuilder versions = HTTP_VERSIONS.stream()
				.map(s -> new StringBuilder().append(s).append('|'))
				.reduce((s1, s2) -> s1.append(s2))
				.get();
		return versions.deleteCharAt(versions.length()-1).toString();
	}
	
	private String httpCode() {
		final Map<Integer, String> HTTP_CODE = new HashMap<>();
		HTTP_CODE.put(200, "OK");
		HTTP_CODE.put(400, "BAD REQUEST");
		StringBuilder codes = new StringBuilder();
		HTTP_CODE.forEach((k,v) -> codes.append("(").append(k).append(" ").append(v).append(")|"));
		return codes.deleteCharAt(codes.length()-1).toString();
	}
	
	@Test
	public void initRegex() {
		String regex = 
				"HTTP/(?<httpversion>" + httpVersions() + ") (?<code>" + httpCode() + ")\r\n"
				+ "Content-type: (?<contenttype>.+); charset=(?<charset>.+)\r\n"
				+ "Content-length: (?<contentlength>\\d+)\r\n" 
				+ "(?<end>\r\n)"
				+ ".*";
		
		String expected = "HTTP/(?<httpversion>1.0|1.1|1.2) (?<code>(400 BAD REQUEST)|(200 OK))\r\n"
				+ "Content-type: (?<contenttype>.+); charset=(?<charset>.+)\r\n"
				+ "Content-length: (?<contentlength>\\d+)\r\n" 
				+ "(?<end>\r\n)"
				+ ".*";
		
		assertEquals(regex, expected);
	}
	
	@Test
	public void initHeaderNoError() throws ServerResponseException {
		ServerResponseHeader header = new ServerResponseHeader(
				"HTTP/1.1 200 OK\r\n"
				+ "Content-type: application/json; charset=utf-8\r\n"
				+ "Content-length: 666\r\n"
				+ "\r\n"
		);
		
		assertEquals(header.getHTTPVersion(), "1.1");
		assertTrue(header.getCode() == 200);
		assertEquals(header.getCodeMSG(), "OK");
		assertEquals(header.getContentType(), "application/json");
		assertEquals(header.getCharset(), "utf-8");
		assertTrue(header.getContentLength() == 666);
		assertTrue(header.getHeaderLength() == 87);
	}
	
	@Test
	public void initHeaderError() throws ServerResponseException {
		exception.expect(ServerResponseException.class);
	    exception.expectMessage("Server response header format is not valid !");
		new ServerResponseHeader(
				"HTTP/1.1 404 NOT FOUND\r\n"
				+ "Content-type: application/json; charset=utf-8\r\n"
				+ "Content-length: 666\r\n"
				+ "\r\n"
		);
	}
	
	@Test
	public void validateHeader() throws ServerResponseException {
		new ServerResponseHeader(
				"HTTP/1.1 200 OK\r\n"
				+ "Content-type: application/json; charset=utf-8\r\n"
				+ "Content-length: 666\r\n"
				+ "\r\n"
		).valid();
	}
	
	@Test
	public void validateHeaderError() throws ServerResponseException {
		exception.expect(ServerResponseException.class);
	    exception.expectMessage("\tHTTP 400 BAD REQUEST\n");
		new ServerResponseHeader(
				"HTTP/1.1 400 BAD REQUEST\r\n"
				+ "Content-type: application/json; charset=utf-8\r\n"
				+ "Content-length: 666\r\n"
				+ "\r\n"
		).valid();
	}
	
}
