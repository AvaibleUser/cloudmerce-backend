package com.ayds.Cloudmerce.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class FailedAuthenticateException extends RuntimeException {

    public FailedAuthenticateException(String message) {
        super(message);
    }
}
