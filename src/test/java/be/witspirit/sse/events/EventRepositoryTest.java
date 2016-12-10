package be.witspirit.sse.events;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Test for EventRepository
 */
public class EventRepositoryTest {

    private EventRepository eventRepo;

    @Before
    public void initRepo() {
        eventRepo = new EventRepository();
    }

    @Test
    public void retrievePublishedEvent() {
        Event event = eventRepo.storeOn("test", "TestMessage");

        assertThat(event.getMessage(), is("TestMessage"));

        assertThat(eventRepo.getEvent(event.getSequenceNr()), is(event));
    }

    @Test
    public void retrieveSince() {
        Event event1 = eventRepo.storeOn("test", "Message 1");
        Event event2 = eventRepo.storeOn("test", "Message 2");
        Event event3 = eventRepo.storeOn("test", "Message 3");
        Event event4 = eventRepo.storeOn("test", "Message 4");

        List<Event> eventsSince = eventRepo.getEventsOnTopicSince("test", event2.getSequenceNr());

        assertThat(eventsSince.size(), is(2));
        assertThat(eventsSince.get(0), is(event3));
        assertThat(eventsSince.get(1), is(event4));
        assertThat(eventRepo.getEvent(event1.getSequenceNr()), is(event1)); // Just showing that the other events are not gone
    }

    @Test
    public void ensureTopicsAreSeparated() {
        Event event1a = eventRepo.storeOn("a", "Message 1A");
        Event event2a = eventRepo.storeOn("a", "Message 2A");

        Event event1b = eventRepo.storeOn("b", "Message 1B");
        Event event2b = eventRepo.storeOn("b", "Message 2B");


        List<Event> topicA = eventRepo.getEventsOnTopicSince("a", EventRepository.BEGINNING);
        assertThat(topicA.size(), is(2));
        assertThat(topicA.get(0), is(event1a));
        assertThat(topicA.get(1), is(event2a));

        List<Event> topicB = eventRepo.getEventsOnTopicSince("b", EventRepository.BEGINNING);
        assertThat(topicB.size(), is(2));
        assertThat(topicB.get(0), is(event1b));
        assertThat(topicB.get(1), is(event2b));
    }

    @Test
    public void askingForAMissingEventReturnsNull() {
        assertThat(eventRepo.getEvent(42), is(nullValue()));

        eventRepo.storeOn("a", "Message 1");
        eventRepo.storeOn("a", "Message 2");
        eventRepo.storeOn("b", "Message 3");

        assertThat(eventRepo.getEvent(42), is(nullValue()));
    }

}
