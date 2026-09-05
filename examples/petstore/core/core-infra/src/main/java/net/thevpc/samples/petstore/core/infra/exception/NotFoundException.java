package net.thevpc.samples.petstore.core.infra.exception;

public class NotFoundException extends AppException {
    public NotFoundException(String message) {
        super(message);
    }
}
