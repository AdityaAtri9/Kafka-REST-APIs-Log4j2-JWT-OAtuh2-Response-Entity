import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class App {
	
	private static final Logger logger = LogManager.getLogger(App.class);
	
	public static void main(String[] args)
	{
		logger.info("Application starts.");
		logger.debug("This is a debug message.");
		logger.warn("This a warning message!");
		logger.fatal("This is a fatal message.");
		logger.error("This is a error message.");
		logger.info("Application finished.");
		
	}

}
