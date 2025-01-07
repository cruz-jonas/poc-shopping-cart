package com.poc.enumeration;

public enum OperationEnum {

    PLUS("PLUS"),
    MINUS("MINUS");

    private final String operation;

    OperationEnum(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }

}
