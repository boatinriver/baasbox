package com.baasbox.dao.exception;

/**
 * Created by boat on 6/11/14.
 */
public class AppAlreadyExistsException extends Exception{
    public AppAlreadyExistsException() {
        super();
    }

    public AppAlreadyExistsException(String s) {
        super(s);
    }

    public AppAlreadyExistsException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public AppAlreadyExistsException(Throwable throwable) {
        super(throwable);
    }
}
