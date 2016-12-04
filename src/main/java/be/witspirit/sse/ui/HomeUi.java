package be.witspirit.sse.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Skeleton UI
 */
@Controller
public class HomeUi {

    @RequestMapping("/")
    public String rootRedirect() {
        return "redirect:/home.html";
    }

    @RequestMapping({"/home*", "/index*"})
    public String homePage() {
        return "home";
    }
}
