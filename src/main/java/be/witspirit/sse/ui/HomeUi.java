package be.witspirit.sse.ui;

import be.witspirit.sse.events.EventApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Skeleton UI
 */
@Controller
public class HomeUi {

    @Autowired
    private EventApi eventApi;

    @RequestMapping("/")
    public String rootRedirect() {
        return "redirect:/home.html";
    }

    @RequestMapping({"/home*", "/index*"})
    public String homePage() {
        return "home";
    }

    @RequestMapping("/publish.html")
    public String publish() {
        return "publish";
    }

    @RequestMapping("/viewEvents.html")
    public String viewEvents() {
        return "viewEvents";
    }



    @RequestMapping("publishMessage")
    public String publishMessage(@RequestParam("destination") String destination, @RequestParam("message") String message) throws IOException {
        eventApi.publish(destination, message);
        return "redirect:/publish.html";
    }

    @RequestMapping("/onTopic/{topic}")
    public ModelAndView onTopic(@PathVariable String topic) {
        Map<String, Object> model = new HashMap<>();
        model.put("topic", topic);
        return new ModelAndView("onTopic", model);
    }
}
