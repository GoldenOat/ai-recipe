package com.ring.offline.api;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ProblemDetail handleUnexpectedException(Exception exception) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
				exception.getMessage());
		problemDetail.setTitle("Internal Server Error");
		problemDetail.setProperty("error", exception.getClass().getSimpleName());
		return problemDetail;
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ProblemDetail handleIllegalArgumentException(IllegalArgumentException exception) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
		problemDetail.setTitle("Bad Request");
		problemDetail.setProperty("errors", Map.of("message", exception.getMessage()));
		return problemDetail;
	}

	@ExceptionHandler(DeviceNotFoundException.class)
	public ProblemDetail handleDeviceNotFoundException(DeviceNotFoundException exception) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
		problemDetail.setTitle("Device Not Found");
		problemDetail.setProperty("deviceId", exception.getDeviceId());
		return problemDetail;
	}

}
