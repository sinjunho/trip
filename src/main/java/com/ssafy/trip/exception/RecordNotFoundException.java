package com.ssafy.trip.exception;

@SuppressWarnings("serial")
public class RecordNotFoundException extends RuntimeException {
    public RecordNotFoundException(String msg) {
        super(msg);
    }
}
