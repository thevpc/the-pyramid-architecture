package net.thevpc.samples.petstore.core.infra.exception;

public class ValidationException extends AppException {
    public ValidationException(String message) {
        super(message);
    }
}
