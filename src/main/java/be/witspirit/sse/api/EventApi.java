package be.witspirit.sse.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Register and retrieve events
 */
@RestController
@RequestMapping("/events")
public class EventApi {

    @RequestMapping(value = "ping", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String ping() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }



}
