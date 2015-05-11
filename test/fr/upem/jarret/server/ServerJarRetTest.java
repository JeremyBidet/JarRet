package fr.upem.jarret.server;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import fr.upem.jarret.client.ComputeException;


/**
 * @author Jeremy
 *
 */
public class ServerJarRetTest {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	
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
	public void serverConfigurationWithJsonFile() {
		ServerConfiguration configuration = new ServerConfiguration("config/JarRetConfig.json");
		
		assertEquals(TimeUnit.MILLISECONDS, configuration.TIMEOUT_TIMEUNIT);

		assertEquals(   80, configuration.LOCAL_PORT);
		assertEquals(   "", configuration.ANSWER_PATH);
		assertEquals(   "", configuration.LOG_PATH);
		assertEquals( 4096, configuration.MAX_ANSWER_SIZE);
		assertEquals(   16, configuration.MAX_THREAD_POOL_SIZE);
		assertEquals( 1000, configuration.CONNECT_TIMEOUT);
		assertEquals(  400, configuration.ACCEPT_TIMEOUT);
		assertEquals( 5000, configuration.COMPUTE_TIMEOUT);
		assertEquals(10000, configuration.SHUTDOWN_TIMEOUT);
	}
	
	@Test
	public void serverConfigurationWithJsonString() {
		ServerConfiguration configuration = new ServerConfiguration(
				"{\n" 
    				+ "\"LocalPort\"         :   80,\n"
    				+ "\"AnswerPath\"        :   \"ans\",\n"
                    + "\"LogPath\"           :   \"log\",\n"
                    + "\"MaxAnswerSize\"     :   4096,\n"
                    + "\"MaxThread\"         :   16,\n"
                    + "\"ConnectTimeout\"    :   1000,\n"
                    + "\"AcceptTimeout\"     :   400,\n"
                    + "\"ComputeTimeout\"    :   5000,\n"
                    + "\"ShutdownTimeout\"   :   10000\n"
				+ "}\n"
		);
		
		assertEquals(TimeUnit.MILLISECONDS, configuration.TIMEOUT_TIMEUNIT);

		assertEquals(   80, configuration.LOCAL_PORT);
		assertEquals("ans", configuration.ANSWER_PATH);
		assertEquals("log", configuration.LOG_PATH);
		assertEquals( 4096, configuration.MAX_ANSWER_SIZE);
		assertEquals(   16, configuration.MAX_THREAD_POOL_SIZE);
		assertEquals( 1000, configuration.CONNECT_TIMEOUT);
		assertEquals(  400, configuration.ACCEPT_TIMEOUT);
		assertEquals( 5000, configuration.COMPUTE_TIMEOUT);
		assertEquals(10000, configuration.SHUTDOWN_TIMEOUT);
	}

}
