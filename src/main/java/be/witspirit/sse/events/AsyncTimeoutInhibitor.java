package be.witspirit.sse.events;

import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;

/**
 * Extension of the ExceptionHandlerExceptionResolver in order to silence the AsyncRequestTimeoutException warnings
 */
@Component
public class AsyncTimeoutInhibitor extends ExceptionHandlerExceptionResolver {

    public AsyncTimeoutInhibitor() {
        setOrder(Ordered.HIGHEST_PRECEDENCE); // Increase the precedence to make sure it gets called
    }

    @Override
    protected void logException(Exception ex, HttpServletRequest request) {
        if (ex instanceof AsyncRequestTimeoutException) {
            logger.debug("Observed AsyncRequestTimeoutException... As expected using SSE.");
        } else {
            super.logException(ex, request);
        }
    }
}
