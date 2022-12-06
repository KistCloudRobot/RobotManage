package net.ion.cloudrobot.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Error 처리를 위한 Controller
 */
@Controller
public class RootController implements ErrorController {
    @GetMapping("/error")
    public String redirectRoot() {
        return "index.html";
    }
}
