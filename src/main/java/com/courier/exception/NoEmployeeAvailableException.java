package com.courier.exception;

public class NoEmployeeAvailableException extends RuntimeException {
    public NoEmployeeAvailableException(String message) { super(message); }
}
