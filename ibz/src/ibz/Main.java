package ibz;

import java.util.logging.Logger;

public class Main {
	
	private static Logger logger = Logger.getLogger(Main.class.getName());

	public static void main(String[] args) {
		logger.fine("visa checker started");
		new Thread(new Ibz()).start();
	}

}
