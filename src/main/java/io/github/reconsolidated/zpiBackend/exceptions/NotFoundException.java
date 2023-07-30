package io.github.reconsolidated.zpiBackend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource doesn't exist.")
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message){
        super(message);
    }
}
