package com.cit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PanelIdException extends RuntimeException {
    public PanelIdException(String exception) {
        super(exception);

    }
}
