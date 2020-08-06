package com.company.resourceapi.exceptions;

public class InternalException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  private final String message;

  public InternalException(String message) {
      this.message = message;
  }


  @Override
  public String getMessage() {
      return message;
  }
}