package tools.descartes.teastore.registry.rest;

/**
 * Datacontainer for the information about the heartbeats.
 * @author Simon
 *
 */
public class HeartbeatInfo {
	
	private long lastHeartbeat;
	
	/**
	 * Contructor for the heartbeat info.
	 */
	public HeartbeatInfo() {
		this.lastHeartbeat = System.currentTimeMillis();
	}
	
	/**
	 * Registers new heartbeat.
	 */
	public void newHeartbeat() {
		this.lastHeartbeat = System.currentTimeMillis();
	}
	
	/**
	 * Checks if the service is still considered alive.
	 * Extensionpoint for more complex mechanisms.
	 * @return true if alive
	 */
	public boolean isAlive() {
		return System.currentTimeMillis() - lastHeartbeat < 10000;
	}
}
