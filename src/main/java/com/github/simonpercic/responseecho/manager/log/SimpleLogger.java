package com.github.simonpercic.responseecho.manager.log;

public interface SimpleLogger {

    void warn(String message);

    void warn(String message, Throwable t);
}
