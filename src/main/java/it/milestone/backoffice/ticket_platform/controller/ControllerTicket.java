package it.milestone.backoffice.ticket_platform.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import it.milestone.backoffice.ticket_platform.model.State;
import it.milestone.backoffice.ticket_platform.model.Ticket;
import it.milestone.backoffice.ticket_platform.repository.CategoryRepository;
import it.milestone.backoffice.ticket_platform.repository.TicketRepository;
import it.milestone.backoffice.ticket_platform.repository.UserRepository;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/dashboard")

public class ControllerTicket {

    @Autowired
    private TicketRepository ticketRepo;

    @Autowired
    private CategoryRepository catRepo;

    @Autowired
    private UserRepository userRepo;

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

    @GetMapping("/show/{id}")
    public String show(@PathVariable("id") Integer id, Model model) {

        Optional<Ticket> ticketOptional = ticketRepo.findById(id);
        if (ticketOptional.isPresent()) {
            model.addAttribute("tickets", ticketOptional.get());
        }

        return "/ticket/show";
    }

    // Metodo GET per visualizzare il form di creazione
    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("ticket", new Ticket()); // Inizializza l'oggetto ticket
        model.addAttribute("allCategory", catRepo.findAll());
        model.addAttribute("availableOperator", userRepo.findByAvailable(true));
        model.addAttribute("states", State.values());
        return "/ticket/create";
    }

    // Metodo POST per gestire l'invio del form
    @PostMapping("/create")
    public String store(@Valid @ModelAttribute("ticket") Ticket formTicket, BindingResult bindingResult, Model model) {

        // Se ci sono errori di validazione, torna al form
        if (bindingResult.hasErrors()) {
            model.addAttribute("allCategory", catRepo.findAll());
            model.addAttribute("availableOperator", userRepo.findByAvailable(true));
            return "/ticket/create";
        }

        // Salva il ticket nel database
        ticketRepo.save(formTicket);

        // Reindirizza alla dashboard
        return "redirect:/dashboard";
    }
}
