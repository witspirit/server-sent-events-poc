package be.witspirit.sse.events;

/**
 * Represents an Event
 */
public class Event {
    private final long sequenceNr;
    private final String message;

    public Event(long sequenceNr, String message) {
        this.sequenceNr = sequenceNr;
        this.message = message;
    }

    public long getSequenceNr() {
        return sequenceNr;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return sequenceNr+":"+message;
    }
}
