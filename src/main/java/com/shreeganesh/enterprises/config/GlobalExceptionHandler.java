package com.shreeganesh.enterprises.config;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(IllegalArgumentException ex,
                                   HttpServletRequest request,
                                   Model model) {
        log.warn("Bad request at {}: {}", request.getRequestURI(), ex.getMessage());
        model.addAttribute("statusCode", 400);
        model.addAttribute("errorTitle", "Request could not be completed");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception ex,
                                  HttpServletRequest request,
                                  Model model) {
        log.error("Unhandled exception at {}", request.getRequestURI(), ex);
        model.addAttribute("statusCode", 500);
        model.addAttribute("errorTitle", "Something went wrong");
        model.addAttribute("errorMessage", "Please try again. If the problem continues, contact the administrator.");
        return "error";
    }

    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public ResponseEntity<Void> handleNoResource() {
        return ResponseEntity.notFound().build();
    }
}
