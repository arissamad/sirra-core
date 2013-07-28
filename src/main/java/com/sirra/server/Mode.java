package com.sirra.server;

/**
 * Designates whether the server is running in production or development mode.
 * 
 * @author aris
 */
public enum Mode {
	Production(), Development();
	
	private Mode() {
		
	}
	
	// Default is production.
	protected static Mode currMode = Mode.Production;
	
	// Use this to find out current mode of server.
	public static Mode get() {
		return currMode;
	}
	
	public static void set(Mode mode) {
		currMode = mode;
	}
}