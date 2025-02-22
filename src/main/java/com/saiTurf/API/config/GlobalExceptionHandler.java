package com.saiTurf.API.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<Map<String, Object>> handleNotFoundException(NoHandlerFoundException ex) {
	    Map<String, Object> response = new HashMap<>();
	    response.put("status", HttpStatus.NOT_FOUND.value());
	    response.put("error", "Not Found");
	    response.put("message", "API not found: " + ex.getRequestURL());

	    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}

}
