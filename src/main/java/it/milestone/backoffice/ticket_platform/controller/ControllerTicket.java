package it.milestone.backoffice.ticket_platform.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import it.milestone.backoffice.ticket_platform.model.Ticket;
import it.milestone.backoffice.ticket_platform.repository.TicketRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/dashboard")
public class ControllerTicket {

    @Autowired
    private TicketRepository ticketRepo;

    @GetMapping
    public String index(Model model, @RequestParam(name = "keyword", required = false) String keyword) {

        List<Ticket> allTickets;

        if (keyword != null && !keyword.isBlank()) {
            allTickets = ticketRepo.findByTitleContainingIgnoreCase(keyword);
            model.addAttribute("keyword", keyword);
        } else {
            allTickets = ticketRepo.findAll();
        }
        model.addAttribute("tickets", allTickets);

        return "/ticket/index";
    }

}
