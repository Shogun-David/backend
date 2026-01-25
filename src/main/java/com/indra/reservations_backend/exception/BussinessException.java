package com.indra.reservations_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class BussinessException extends RuntimeException{
    public BussinessException(String message){
        super(message);
    }
}
