package br.com.hello.activiti.webcomp.exception;

import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

import org.apache.log4j.Logger;

public class ExceptionHandler extends ExceptionHandlerWrapper {

	private Logger LOGGER = Logger.getLogger(getClass());

	private javax.faces.context.ExceptionHandler wrapped;

	public ExceptionHandler(javax.faces.context.ExceptionHandler wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public javax.faces.context.ExceptionHandler getWrapped() {
		return wrapped;
	}

	@Override
	public void handle() throws FacesException {
		Iterator iterator = getUnhandledExceptionQueuedEvents().iterator();

		while (iterator.hasNext()) {
			ExceptionQueuedEvent event = (ExceptionQueuedEvent) iterator.next();
			ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event
					.getSource();

			Throwable throwable = context.getException();

			FacesContext fc = FacesContext.getCurrentInstance();

			try {
				Flash flash = fc.getExternalContext().getFlash();

				// Put the exception in the flash scope to be displayed in the
				// error
				// page if necessary ...
				flash.put("errorDetails", throwable.getMessage());

				LOGGER.error("the error is put in the flash: "
						+ throwable.getMessage());

				NavigationHandler navigationHandler = fc.getApplication()
						.getNavigationHandler();

				navigationHandler.handleNavigation(fc, null,
						"error?faces-redirect=true");

				fc.renderResponse();
			} finally {
				iterator.remove();
			}
		}

		// Let the parent handle the rest
		getWrapped().handle();
	}
}
