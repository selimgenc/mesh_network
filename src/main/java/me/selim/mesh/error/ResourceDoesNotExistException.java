package me.selim.mesh.error;

public class ResourceDoesNotExistException extends RuntimeException {
    public ResourceDoesNotExistException(String message) {
        super(message);
    }
}
