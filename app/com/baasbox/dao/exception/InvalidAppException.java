package com.baasbox.dao.exception;

/**
 * Created by boat on 6/11/14.
 */
public class InvalidAppException extends Exception {
    public InvalidAppException() {
        super();
    }

    public InvalidAppException(String s) {
        super(s);
    }

    public InvalidAppException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public InvalidAppException(Throwable throwable) {
        super(throwable);
    }
}
