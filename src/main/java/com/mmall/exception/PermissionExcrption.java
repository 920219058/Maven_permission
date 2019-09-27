package com.mmall.exception;

public class PermissionExcrption extends RuntimeException{
    public PermissionExcrption() {
        super();
    }

    public PermissionExcrption(String message) {
        super(message);
    }

    public PermissionExcrption(String message, Throwable cause) {
        super(message, cause);
    }

    public PermissionExcrption(Throwable cause) {
        super(cause);
    }

    protected PermissionExcrption(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
