package com.mch.unicoursehub.exceptions.responses;

import lombok.Builder;
import lombok.Data;

/**
 * Represents an error with a specific field name and error message.
 * This class is used to capture detailed error information.
 */
@Data
@Builder
public class Error {

    private String fieldName;
    private String errorMessage;

}