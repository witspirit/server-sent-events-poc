package be.witspirit.sse.events;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Stores events on given topics
 */
@Repository
public class EventRepository {
    public static final long BEGINNING = -1L;

    private static final AtomicLong sequenceNr = new AtomicLong(0);
    private Map<String, List<Event>> eventsPerTopic = new HashMap<>();

    public Event storeOn(String topic, String message) {
        Event event = new Event(sequenceNr.getAndIncrement(), message);

        eventsPerTopic.putIfAbsent(topic, new ArrayList<>()); // Wouldn't it be nice if it returned the now actual value...
        List<Event> eventsOnTopic = eventsPerTopic.get(topic);
        eventsOnTopic.add(event);

        return event;
    }

    public Event getEvent(long eventNr) {
        // Ok, since this is in memory, we are going to do this as simple as possible
        for (List<Event> events : eventsPerTopic.values()) {
            for (Event event : events) {
                if (event.getSequenceNr() == eventNr) {
                    return event;
                }
            }
        }
        return null;
    }

    public List<Event> getEventsOnTopicSince(String topic, long sequenceNr) {
        List<Event> events = eventsPerTopic.getOrDefault(topic, new ArrayList<>());
        List<Event> eventsSince = new ArrayList<>();
        for (Event event : events) {
            if (event.getSequenceNr() > sequenceNr) {
                eventsSince.add(event);
            }
        }

        return eventsSince;
    }

}
