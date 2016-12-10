package be.witspirit.sse.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Just some utilities to verify the system
 */
@RestController
public class UtilityApi {
    private static final Logger LOG = LoggerFactory.getLogger(UtilityApi.class);

    @RequestMapping(value = "/ping", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String ping() throws UnknownHostException {
        LOG.debug("Ping");
        return InetAddress.getLocalHost().getHostAddress();
    }


}
