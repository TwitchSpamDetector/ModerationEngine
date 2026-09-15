package com.twitchspamdetector.moderationengine.exception;

/**
 * Se lanza cuando no se pudo obtener la decision final de la API central.
 * El GlobalExceptionHandler la traduce a HTTP 502, tal como define el contrato.
 */
public class CentralApiUnavailableException extends RuntimeException {

    public CentralApiUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
