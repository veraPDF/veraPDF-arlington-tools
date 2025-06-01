package org.verapdf.arlington;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class LogsHandler extends Handler {

    int logsNumber = 0;

    @Override
    public void publish(LogRecord record) {
        logsNumber++;
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }

}