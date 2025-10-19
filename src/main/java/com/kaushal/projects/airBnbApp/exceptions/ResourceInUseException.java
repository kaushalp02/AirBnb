package com.kaushal.projects.airBnbApp.exceptions;

public class ResourceInUseException extends RuntimeException{
    public ResourceInUseException(String message) {
        super(message);
    }
}
