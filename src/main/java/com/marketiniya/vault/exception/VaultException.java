package com.marketiniya.vault.exception;

public class VaultException extends RuntimeException {
    private final int statusCode;
    
    public VaultException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
    
    public VaultException(String message, Throwable cause, int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
}
