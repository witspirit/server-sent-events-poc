package be.witspirit.sse.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Manages SseEmitter sessions and allows delivery of events to them
 */
@Component
public class EventDispatcher {
    private static final Logger LOG = LoggerFactory.getLogger(EventDispatcher.class);

    private Map<String, List<StreamListener>> listenersPerTopic = new HashMap<>();

    public synchronized void register(String topic, StreamListener listener) {
        listenersPerTopic.putIfAbsent(topic, new ArrayList<>());
        listenersPerTopic.get(topic).add(listener);

        sanitize();
    }

    private synchronized void sanitize() {
        for (String topic : listenersPerTopic.keySet()) {
            List<StreamListener> listeners = listenersPerTopic.get(topic);
            List<StreamListener> liveListeners = listeners.stream().filter(l -> l.getStatus() == ListenerStatus.LIVE).collect(Collectors.toList());
            if (liveListeners.isEmpty()) {
                listenersPerTopic.remove(topic);
            } else {
                listenersPerTopic.put(topic, liveListeners);
            }
        }
    }

    public void dispatch(String topic, Event event) {
        List<StreamListener> listeners;
        synchronized (this) {
            listeners = new ArrayList<>(listenersPerTopic.getOrDefault(topic, new ArrayList<>()));
        }
        listeners.forEach(l -> l.deliver(event));
    }
}
