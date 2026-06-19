package com.proyect.imagenes.Exceptions;

public class ImageAlreadyExistsException extends RuntimeException {
    public ImageAlreadyExistsException(String message) {
        super(message);
    }
}