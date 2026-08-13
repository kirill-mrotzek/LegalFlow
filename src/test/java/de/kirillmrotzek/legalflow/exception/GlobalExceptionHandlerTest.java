package de.kirillmrotzek.legalflow.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    @Test
    void handleContractNotFound_shouldReturn404() {

        ContractNotFoundException exception =
                new ContractNotFoundException(999L);

        ResponseEntity<ErrorResponse> response =
                handler.handleContractNotFound(exception);

        assertEquals(404, response.getStatusCode().value());

        assertNotNull(response.getBody());

        assertEquals(404, response.getBody().getStatus());

        assertEquals(
                "Contract with id 999 not found",
                response.getBody().getMessage()
        );
    }

    @Test
    void handleValidationException_shouldReturn400() {

        BindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "contractRequest");

        bindingResult.addError(
                new FieldError(
                        "contractRequest",
                        "title",
                        "must not be blank"
                )
        );

        bindingResult.addError(
                new FieldError(
                        "contractRequest",
                        "contractNumber",
                        "must not be blank"
                )
        );

        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ValidationErrorResponse> response =
                handler.handleValidationException(exception);

        assertEquals(400, response.getStatusCode().value());

        assertNotNull(response.getBody());

        assertEquals(400, response.getBody().getStatus());

        assertEquals(
                List.of(
                        "title: must not be blank",
                        "contractNumber: must not be blank"
                ),
                response.getBody().getErrors()
        );
    }
}
