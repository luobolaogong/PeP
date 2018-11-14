//package pep.utilities;
//
//import java.util.logging.Filter;
//import java.util.logging.LogRecord;
//
//// This is just a test.  The idea is to be able to pull out timing log messages to make it clear where
//// page element processing is hanging.
//public class LoggingTimingFilter implements Filter {
//    @Override
//    public boolean isLoggable(LogRecord record) {
//        if (record == null) {
//            return false;
//        }
//        String message = record.getMessage() == null ? "" : record.getMessage();
//        if(message.contains("timing")) {
//            return true;
//        }
//        return false;
//    }
//}
