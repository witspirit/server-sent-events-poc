package be.witspirit.sse.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Register and retrieve events
 */
@RestController
@RequestMapping("/events")
public class EventApi {
    private static final Logger LOG = LoggerFactory.getLogger(EventApi.class);

    private static Map<String, SseEmitter> sseEmitters = new HashMap<>();


    @RequestMapping(value = "/ping", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String ping() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

    @RequestMapping("/subscribe/{id}")
    public SseEmitter subscribe(@PathVariable("id") String id) {
        LOG.info("Subscription received for " + id);

        SseEmitter subscription = sseEmitters.get(id);
        if (subscription == null) {
            LOG.info("Fresh subscription for " + id);
        } else {
            LOG.info("Replacing existing subscription for " + id);
        }
        subscription = new SseEmitter(30_000L);
        sseEmitters.put(id, subscription);

        return subscription;
    }

    @RequestMapping("/unsubscribe/{id}")
    public void unsubscribe(@PathVariable("id") String id) {
        SseEmitter subscription = sseEmitters.remove(id);
        subscription.complete();
    }

    @RequestMapping(value = "/deliver/{id}", method = RequestMethod.POST)
    public void deliver(@PathVariable("id") String id, @RequestBody String message) throws IOException {
        SseEmitter destination = sseEmitters.get(id);
        if (destination == null) {
            throw new IllegalStateException(id + " is not subscribed.");
        }

        destination.send(message);
    }

    @RequestMapping(value = "/subscribers")
    public List<String> getSubscribers() {
        return new ArrayList<>(sseEmitters.keySet());
    }


}
