package be.witspirit.sse.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * Register and retrieve events
 */
@RestController
@RequestMapping("/events")
public class EventApi {
    private static final Logger LOG = LoggerFactory.getLogger(EventApi.class);

    private final EventRepository eventRepo;
    private final EventDispatcher dispatcher;

    @Autowired
    public EventApi(EventRepository eventRepo, EventDispatcher dispatcher) {
        this.eventRepo = eventRepo;
        this.dispatcher = dispatcher;
    }

    @RequestMapping(value = "/{topic}", method = RequestMethod.POST, consumes = {MediaType.TEXT_PLAIN_VALUE})
    public Event publish(@PathVariable String topic, @RequestBody String message) {
        Event event = eventRepo.storeOn(topic, message);
        dispatcher.dispatch(topic, event);
        return event;
    }

    @RequestMapping(value = "/{topic}", method = RequestMethod.GET)
    public List<Event> getEventsOnTopicSince(@PathVariable String topic, @RequestParam(required=false) String since) {
        long sequenceNr = EventRepository.BEGINNING;
        if (since != null) {
            try {
                sequenceNr = Long.parseLong(since);
            } catch (NumberFormatException e) {
                LOG.debug("Failed to convert "+since+ " to a sequence number.", e);
                throw new IllegalArgumentException("Failed to interpret "+since+" as a valid event sequence number.");
            }
        }
        return eventRepo.getEventsOnTopicSince(topic, sequenceNr);
    }

    @RequestMapping(value = "/{topic}/stream", method = RequestMethod.GET) // Last-Event-ID is the standard way for EventSource to indicate the last message it has seen
    public SseEmitter streamEventsOnTopicSince(@PathVariable String topic, @RequestHeader(value="Last-Event-ID", required=false) String since) {

        StreamListener listener = new StreamListener(new SseEmitter());
        dispatcher.register(topic, listener);

        List<Event> currentEvents = getEventsOnTopicSince(topic, since);
        currentEvents.forEach(e -> listener.deliver(e));

        return listener.getSse();
    }

    @ExceptionHandler(AsyncRequestTimeoutException.class)
    public void disregardAsyncRequestTimeouts() {
        LOG.info("Received an AsyncRequestTimeoutException... as expected using SSE usage");
        // To silence warnings in the logs generated by the ExceptionHandlerExceptionResolver I have added a replacement AsyncTimeoutInhibitor
    }

}
