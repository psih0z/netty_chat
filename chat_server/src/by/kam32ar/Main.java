package by.kam32ar;

import by.kam32ar.server.ServerManager;
import by.kam32ar.server.utils.Log;

public class Main {

	public static void main(String[] args) throws Exception {

		args = new String[] { "settings.xml" };

		final ServerManager manager = new ServerManager();
		manager.init(args);

		Log.info("Starting server...");
		Log.logSystemInfo();

		manager.start();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				Log.info("Shutting down server...");
				manager.stop();
			}
		});
	}

}
