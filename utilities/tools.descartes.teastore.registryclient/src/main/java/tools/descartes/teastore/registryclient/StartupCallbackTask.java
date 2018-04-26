package tools.descartes.teastore.registryclient;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.loadbalancer.Server;

public class StartupCallbackTask implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(RegistryClient.class);
	
	private Service requestedService;
	private StartupCallback callback;
	private Service myService;
	
	public StartupCallbackTask(Service requestedService, StartupCallback callback, Service myService) {
		this.requestedService = requestedService;
		this.callback = callback;
		this.myService = myService;
	}
	
	@Override
	public void run() {
		try {
	    	List<Server> servers;
	    	do {
	    		servers = RegistryClient.getClient().getServersForService(requestedService);
	    		if (servers == null || servers.isEmpty()) {
	    			try {
	    				if (servers == null) {
	    					LOG.info("Registry not online. " + myService + " is waiting for it to come online");
	    				} else {
	    					LOG.info(requestedService.getServiceName() + " not online. "
	    							+ myService + " is waiting for it to come online");
	    				}
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	    		}
	    	} while (servers == null || servers.isEmpty());
	    		callback.callback();
			} catch (Exception e) {
				e.printStackTrace();
				throw(e);
			}
	}

}
