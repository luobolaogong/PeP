package pep.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class LoggingFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        StringBuffer buffer = new StringBuffer();
        if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
            buffer.append("Thread: " + record.getThreadID());
            buffer.append(" message: >" + record.getMessage() + "<");
            buffer.append(" level: " + record.getLevel());
            buffer.append(" logger: " + record.getLoggerName());
            buffer.append(" date: " + formatDateTime(record.getMillis()));
            buffer.append(" class: " + record.getSourceClassName());
            buffer.append(" method: " + record.getSourceMethodName());
        }
        if (record.getLevel().intValue() == Level.INFO.intValue()) {
            buffer.append(" message: " + record.getMessage());
            buffer.append(" date: " + formatDateTime(record.getMillis()));
            buffer.append(" class: " + record.getSourceClassName());
            buffer.append(" method: " + record.getSourceMethodName());
        }
        if (record.getLevel().intValue() <= Level.FINE.intValue()) {
            buffer.append(formatDateTime(record.getMillis()));
            buffer.append(" " + record.getMessage());
        }
        return buffer.toString();
    }

    private String formatDateTime(long millisecs) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
        Date recordDate = new Date(millisecs);
        return dateFormat.format(recordDate);
    }
}
