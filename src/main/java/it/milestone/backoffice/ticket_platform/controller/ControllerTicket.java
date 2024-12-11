package it.milestone.backoffice.ticket_platform.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import it.milestone.backoffice.ticket_platform.model.Note;
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

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model) {

        model.addAttribute("newNote", new Note());
        model.addAttribute("ticket", ticketRepo.findById(id).get());
        model.addAttribute("allCategory", catRepo.findAll());
        model.addAttribute("availableOperator", userRepo.findByAvailable(true));
        model.addAttribute("states", State.values());

        return "/ticket/edit";
    }

    @PostMapping("/edit/{id}")
    public String update(@Valid @ModelAttribute("ticket") Ticket formTicket,
            BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "/ticket/edit";
        }

        ticketRepo.save(formTicket);

        return "redirect:/dashboard";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id) {

        ticketRepo.deleteById(id);

        return "redirect:/dashboard";
    }

    @GetMapping("/{id}/note")
    public String newNote(@PathVariable("id") Integer id, Model model) {

        // Recupera il ticket
        Ticket ticket = ticketRepo.findById(id).get();

        Note note = new Note();
        note.setTicket(ticket);
        note.setCreationDate(LocalDate.now());

        model.addAttribute("ticketId", ticket.getId());
        model.addAttribute("note", note);

        return "note/create";
    }

}
