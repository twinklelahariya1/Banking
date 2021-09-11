package com.twinkle.banking.exception;

import com.twinkle.banking.model.response.APIResponse;
import com.twinkle.banking.model.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(DataNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<APIResponse> resourceNotFoundException(DataNotFoundException ex) {
        ErrorResponse message = new ErrorResponse(
                ex.getMessage());

        return Mono.just(new APIResponse(404, null, message));
    }

    @ExceptionHandler(InvalidRequestException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public Mono<APIResponse> resourceNotFoundException(InvalidRequestException ex) {
        ErrorResponse message = new ErrorResponse(
                ex.getMessage());

        return Mono.just(new APIResponse(422, null, message));
    }

    @ExceptionHandler(TransactionTimedOut.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<APIResponse> resourceNotFoundException() {

        return Mono.just(new APIResponse(204, null, null));
    }

}
