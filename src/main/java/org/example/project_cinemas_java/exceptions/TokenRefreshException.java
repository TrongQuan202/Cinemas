package org.example.project_cinemas_java.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class TokenRefreshException extends Exception{


    public TokenRefreshException(String message) {
        super(message);
    }
}
