package com.atipera.demo.dto;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}