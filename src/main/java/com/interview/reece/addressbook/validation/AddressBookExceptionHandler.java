package com.interview.reece.addressbook.validation;

import com.interview.reece.addressbook.exception.AddressBookNotFoundException;
import com.interview.reece.addressbook.exception.AddressBookRuntimeException;
import com.interview.reece.addressbook.exception.CustomerNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class AddressBookExceptionHandler {
    private static final String ERRORS_KEY = "errors";

    @ExceptionHandler(exception = {AddressBookNotFoundException.class, CustomerNotFoundException.class})
    public ResponseEntity<Map<String, List<String>>> handleNotFoundException(final AddressBookRuntimeException addressBookRuntimeException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(ERRORS_KEY, Collections.singletonList(addressBookRuntimeException.getMessage())));
    }

    @ExceptionHandler(exception = MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleInvalidRequestException(final MethodArgumentNotValidException methodArgumentNotValidException) {
        final List<String> errors = methodArgumentNotValidException.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(ERRORS_KEY, errors));
    }

}
