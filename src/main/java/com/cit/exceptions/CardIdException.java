package com.cit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CardIdException extends RuntimeException {
    public CardIdException(String exception) {
        super(exception);

    }
}
