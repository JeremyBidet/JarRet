package fr.upem.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import fr.upem.jarret.server.ServerResponseContent;
import fr.upem.jarret.server.ServerResponseException;
import fr.upem.jarret.server.ServerResponseException.ServerResponseThrowable;


/**
 * @author Jeremy
 */
public class ServerResponseContentTest {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();

	
	@Test
	public void initContentNoError() throws ServerResponseException, IOException {
		ServerResponseContent content = new ServerResponseContent(
				"\n{\n"
	    				+ "\"JobId\": \"23571113\",\n"
	    				+ "\"WorkerVersion\": \"1.0\",\n"
	    				+ "\"WorkerURL\": \"http://igm.univ-mlv.fr/~carayol/WorkerPrimeV1.jar\",\n"
	    				+ "\"WorkerClassName\": \"upem.workerprime.WorkerPrime\",\n"
	    				+ "\"Task\":        100\n"
					+ "}"
		);
		
		assertTrue(content.getJobID() == 23571113L);
		assertEquals(content.getWorkerVersion(), "1.0");
		assertEquals(content.getWorkerUrl(), "http://igm.univ-mlv.fr/~carayol/WorkerPrimeV1.jar");
		assertEquals(content.getWorkerClassname(), "upem.workerprime.WorkerPrime");
		assertTrue(content.getTask() == 100);
	}
	
	@Test
	public void initContentError() throws ServerResponseException, IOException {
		exception.expect(ServerResponseException.class);
	    exception.expectMessage("Server response content format is not valid !");
	    new ServerResponseContent(
				"Hyrd\n{\n"
    				+ "\"JobId\": \"23571113\",\n"
    				+ "\"WorkerVersion\": \"1.0\",\n"
    				+ "\"WorkerURL\": \"http://igm.univ-mlv.fr/~carayol/WorkerPrimeV1.jar\",\n"
    				+ "\"WorkerClassName\": \"upem.workerprime.WorkerPrime\",\n"
    				+ "\"Task\":        100\n"
				+ "}"
		);
	}
	
	@Test
	public void initContentNoTask() throws ServerResponseException, IOException {
		exception.expect(ServerResponseException.class);
	    exception.expectMessage("Server has no task to compute for the moment !");
	    new ServerResponseContent(
				"\n{\n"
	    			+ "\"ComeBackInSeconds\" : 300\n"
				+ "}"
		);
	}
	
	@Test
	public void initContentNoTaskWithTimeout() throws ServerResponseException, IOException {
		try {
			new ServerResponseContent(
					"\n{\n"
						+ "\"ComeBackInSeconds\" : 300\n"
					+ "}"
			);
		} catch (ServerResponseException e) {
			if( e.getCause().getMessage() == "REQUEST Timeout" ) {
				long timeout = ((ServerResponseThrowable) e.getCause()).getTimeout();
				long time = System.currentTimeMillis();
				while( System.currentTimeMillis() - time < timeout );
			}
		}
	}
	
}
