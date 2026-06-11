package org.example;

public class InvalidQueryException extends RuntimeException {

    private final String field;
    private final Object invalidValue;

    public InvalidQueryException(String field, Object invalidValue, String message) {
        super(message);
        this.field = field;
        this.invalidValue = invalidValue;
    }

    public String getField() { return field; }
    public Object getInvalidValue() {                                                                               return invalidValue; }

    @Override
    public String toString() {
        return "InvalidQueryException {" +
                "field='" + field + "'" +
                ", invalidValue='" + invalidValue + "'" +
                ", message='" + getMessage() + "'" +
                "}";
    }
}