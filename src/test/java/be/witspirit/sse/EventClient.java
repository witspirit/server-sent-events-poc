package be.witspirit.sse;

import org.glassfish.jersey.media.sse.EventListener;
import org.glassfish.jersey.media.sse.EventSource;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.IOException;

/**
 * Starts a client to listen for events
 */
public class EventClient {
    private static final Logger LOG = LoggerFactory.getLogger(EventClient.class);

    @Test
    public void listenForTest() {
        listen("test");
    }

    private void listen(String id) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080/events/subscribe/"+id);
        EventSource eventSource = EventSource.target(target).build();
        EventListener listener = new EventListener() {
            @Override
            public void onEvent(InboundEvent inboundEvent) {
                LOG.info(inboundEvent.getName()+" = "+inboundEvent.readData(String.class));
            }
        };
        eventSource.register(listener);

        LOG.info("Opening connection...");
        eventSource.open();



        try {
            System.in.read();
        } catch (IOException e) {
        }
        eventSource.close();
    }
}
