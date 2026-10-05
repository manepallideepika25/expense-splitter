package com.deepika.expense_splitter.exception;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String message)
    {
        super(message);
    }
}
