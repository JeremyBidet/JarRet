package fr.upem.jarret;

import java.net.MalformedURLException;

import fr.upem.jarret.worker.Worker;
import fr.upem.jarret.worker.WorkerFactory;

// TODO class

/**
 * 
 * @author Jeremy
 * @author Enzo
 */
public class UserJarRet {

	private final String jar_url;
	private final String class_name;
	private final Worker worker;

	public UserJarRet() {
		this("http://igm.univ-mlv.fr/~carayol/WorkerPrimeV1.jar", "upem.workerprime.WorkerPrime");
	}

	public UserJarRet(String jar_url, String class_name) {
		this.jar_url = jar_url;
		this.class_name = class_name;
		Worker worker_tmp = null;
		try {
			worker_tmp = WorkerFactory.getWorker(this.jar_url, this.class_name);
		} catch( MalformedURLException | ClassNotFoundException | IllegalAccessException | InstantiationException e ) {
			System.err.println(e.getMessage());
		} finally {
			this.worker = worker_tmp;
		}
	}
	
	public UserJarRet(Worker worker) {
		this.jar_url = null;
		this.class_name = null;
		this.worker = worker;
	}
	
	public String getJarUrl() {
		return this.jar_url;
	}
	
	public String getClassName() {
		return this.class_name;
	}
	
	public Worker getWorker() {
		return this.worker;
	}

}
