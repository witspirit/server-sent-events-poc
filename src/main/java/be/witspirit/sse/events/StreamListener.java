package be.witspirit.sse.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.UUID;

/**
 * Keeps track of something listening to our stream
 */
public class StreamListener {
    private static final Logger LOG = LoggerFactory.getLogger(StreamListener.class);

    private final SseEmitter sse;

    private final UUID id; // Just added for identification
    private ListenerStatus status;

    public StreamListener(SseEmitter sse) {
        this.sse = sse;
        this.id = UUID.randomUUID();
        this.status = ListenerStatus.LIVE;

        this.sse.onTimeout(this::onTimeout);
        this.sse.onCompletion(this::onCompletion);
    }

    private void onCompletion() {
        LOG.info(id+" completed");
        this.status = ListenerStatus.COMPLETE;
    }

    private void onTimeout() {
        LOG.info(id+" timed out");
        this.status = ListenerStatus.TIMED_OUT;
    }

    public SseEmitter getSse() {
        return sse;
    }

    public UUID getId() {
        return id;
    }

    public ListenerStatus getStatus() {
        return status;
    }

    public void deliver(Event e) {
        try {
            if (status == ListenerStatus.LIVE) {
                sse.send(SseEmitter.event().id(e.getSequenceNr() + "").name("message").data(e.getMessage()));
            }
        } catch (IOException ex) {
            LOG.warn("Failed to dispatch " + e + " to " + this, ex);
            // Should this change our state ?
        } catch (IllegalStateException ise) {
            LOG.warn("Failed to dispatch " + e + " to " + this, ise);
            this.status = ListenerStatus.COMPLETE; // Most likely reason for ISE
        }
    }

    @Override
    public String toString() {
        return "Listener("+id+":"+status+")";
    }
}
