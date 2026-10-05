package com.apiece.coupon.support;

public class QueueFullException extends RuntimeException {
  public QueueFullException(String message) {
    super(message);
  }
}
