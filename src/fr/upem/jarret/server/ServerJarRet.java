package fr.upem.jarret.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;

// TODO class

/**
 * This is
 * 
 * @author Enzo
 * @author Jeremy
 */
public class ServerJarRet {

	private final int MAX_ANSWER_SIZE = 4096;
	private ServerSocketChannel ssc;
	public final ByteBuffer bb = ByteBuffer.allocateDirect(MAX_ANSWER_SIZE);
	
	public ServerJarRet() throws IOException {
		this.ssc = ServerSocketChannel.open();
		this.ssc.configureBlocking(false);
		this.ssc.bind(null);
	}
	
	public void start() {
		// TODO server start
	}
	
	public static void main(String args[]) {
		// TODO server main
	}

}
