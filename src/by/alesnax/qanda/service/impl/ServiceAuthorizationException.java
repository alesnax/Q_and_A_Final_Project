package by.alesnax.qanda.service.impl;

/**
 * Created by alesnax on 12.12.2016.
 */
public class ServiceAuthorizationException extends ServiceException {
    private static final long serialVersionUID = 1L;

    public ServiceAuthorizationException(String message) {
        super(message);
    }

    public ServiceAuthorizationException(String message, Exception e) {
        super(message, e);
    }
}