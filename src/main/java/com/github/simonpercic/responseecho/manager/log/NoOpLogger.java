package com.github.simonpercic.responseecho.manager.log;

public class NoOpLogger implements SimpleLogger {

    @Override
    public void warn(String message) {
        // no-op
    }

    @Override
    public void warn(String message, Throwable t) {
        // no-op
    }
}
