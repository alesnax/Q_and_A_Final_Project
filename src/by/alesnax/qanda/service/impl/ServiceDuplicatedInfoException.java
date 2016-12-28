package by.alesnax.qanda.service.impl;

public class ServiceDuplicatedInfoException extends ServiceException {
    private static final long serialVersionUID = 1L;

    public ServiceDuplicatedInfoException(String message) {
        super(message);
    }

    public ServiceDuplicatedInfoException(String message, Exception e) {
        super(message, e);
    }
}
