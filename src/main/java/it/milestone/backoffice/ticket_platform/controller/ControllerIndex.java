package it.milestone.backoffice.ticket_platform.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping("/")
public class ControllerIndex {

    @GetMapping
    public String index() {
        return "redirect:/dashboard";
    }

}
