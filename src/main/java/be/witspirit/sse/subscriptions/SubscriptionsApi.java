package be.witspirit.sse.subscriptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Keeps track of the ongoing subscriptions
 */
@RestController
@RequestMapping("/subscriptions")
public class SubscriptionsApi {
    private static final Logger LOG = LoggerFactory.getLogger(SubscriptionsApi.class);

    private static Map<String, SseEmitter> subscribers = new HashMap<>();

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<String> getSubscribers() {
        return new ArrayList<>(subscribers.keySet());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public SseEmitter getSubscription(@PathVariable("id") String id) {
        return subscribers.get(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public SseEmitter subscribe(@PathVariable("id") String id) {
        LOG.info("Subscription received for " + id);

        SseEmitter subscription = subscribers.get(id);
        if (subscription == null) {
            LOG.info("Fresh subscription for " + id);
        } else {
            LOG.info("Replacing existing subscription for " + id);
        }
        subscription = new SseEmitter(30_000L);
        SubscriptionMonitor monitor = new SubscriptionMonitor(id);
        subscription.onCompletion(monitor::onComplete);
        subscription.onTimeout(monitor::onTimeout);
        subscribers.put(id, subscription);

        return subscription;
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void unSubscribe(@PathVariable("id") String id) {
        SseEmitter subscription = subscribers.remove(id);
        subscription.complete();
    }

    private class SubscriptionMonitor {
        private String id;

        public SubscriptionMonitor(String id) {
            this.id = id;
        }

        public void onComplete() {
            LOG.info("Subscription with id "+id +" completed");
        }

        public void onTimeout() {
            LOG.info("Subscription with id "+id +" timed out.");
        }
    }
}
