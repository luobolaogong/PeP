package pep.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;

public class LoggingTimer { // not sure about this.  Not sure about making the following public either.
    public static final Logger timerLogger = Logger.getLogger(LoggingTimer.class.getName()); // logger for this package, but should inherit from rootLogger
//    static {
//        try {
//            logManager.readConfiguration(new FileInputStream("Resources/logging.properties"));
//        }
//        catch (IOException e) {
//            //rootLogger.log(Level.SEVERE, "Error in loading log configuration", e);
//            if (Arguments.debug) System.out.println("Error in loading log configuration" + e.getMessage());
//        }
//    }

}
