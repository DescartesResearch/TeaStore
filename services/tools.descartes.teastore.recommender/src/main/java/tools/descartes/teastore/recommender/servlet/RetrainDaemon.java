/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tools.descartes.teastore.recommender.servlet;

import tools.descartes.teastore.registryclient.RegistryClient;
import tools.descartes.teastore.registryclient.Service;
import tools.descartes.teastore.registryclient.StartupCallback;

/**
 * DaemonThread for periodic retraining if required.
 * 
 * @author Johannes Grohmann
 */
public class RetrainDaemon extends Thread {

	/**
	 * The time between retraining in milliseconds.
	 */
	private long looptime;

	/**
	 * Constructor.
	 * 
	 * @param looptime
	 *            The time between retraining in milliseconds
	 */
	public RetrainDaemon(long looptime) {
		super();
		// set as daemon thread
		setDaemon(true);
		this.looptime = looptime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		super.run();
		// repeat until stopped
		while (true) {
			try {
				Thread.sleep(looptime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// wait for the persistance service and then retrain
			RegistryClient.getClient().runAfterServiceIsAvailable(Service.PERSISTENCE, new StartupCallback() {
				@Override
				public void callback() {
					TrainingSynchronizer.getInstance().retrieveDataAndRetrain();
				}
			}, Service.RECOMMENDER);
		}

	}

}
