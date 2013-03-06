package org.gatein.wcm.ui.exceptions;

import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

import org.jboss.logging.Logger;

public class WcmDefaultExceptionHandler extends ExceptionHandlerWrapper {

    private static final Logger log = Logger.getLogger("org.gatein.wcm.ui.exceptions");

    private ExceptionHandler wrapped;

    protected WcmDefaultExceptionHandler(ExceptionHandler wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public ExceptionHandler getWrapped() {
        return wrapped;
    }

    @Override
    public void handle() throws FacesException {
        Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator();
        // Processing exceptions
        while (i.hasNext()) {
            ExceptionQueuedEvent exceptionQueuedEvent = (ExceptionQueuedEvent) i.next();
            ExceptionQueuedEventContext context = (ExceptionQueuedEventContext)exceptionQueuedEvent.getSource();
            Throwable t = context.getException();
            log.error("Exception: " + t, t);
            i.remove();
        }
        getWrapped().handle();
    }

    protected String handleUnexpected(FacesContext facesContext, final Throwable t) {
        log.error("Unexpected error: " + t, t);
        return "org.gatein.wcm.ui.exception.UnexpectedException";
    }





}
