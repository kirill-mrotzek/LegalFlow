package de.kirillmrotzek.legalflow.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ValidationErrorResponse {

    private int status;

    private List<String> errors;
}
