package com.deepika.expense_splitter.exception;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message)
    {
        super(message);
    }
}
