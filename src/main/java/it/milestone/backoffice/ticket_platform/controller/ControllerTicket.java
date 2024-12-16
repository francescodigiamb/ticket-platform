package it.milestone.backoffice.ticket_platform.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import it.milestone.backoffice.ticket_platform.model.Note;
import it.milestone.backoffice.ticket_platform.model.State;
import it.milestone.backoffice.ticket_platform.model.Ticket;
import it.milestone.backoffice.ticket_platform.model.User;
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

        // Recupera l'utente loggato dal contesto di sicurezza
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Recupera l'utente dal repository
        Optional<User> optionalUser = userRepo.findByUsername(username);
        if (!optionalUser.isPresent()) {
            throw new RuntimeException("User not found for username: " + username);
        }

        List<Ticket> tickets = null;

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
            if (keyword != null && !keyword.isBlank()) {
                tickets = ticketRepo.findByTitleContainingIgnoreCase(keyword);
                model.addAttribute("keyword", keyword);
            } else {
                tickets = ticketRepo.findAll();
            }
        } else {
            if (keyword != null && !keyword.isBlank()) {
                tickets = ticketRepo.findByOperatorUsernameAndTitleContainingIgnoreCase(
                        optionalUser.get().getUsername(),
                        keyword);
                model.addAttribute("keyword", keyword);
            } else {
                tickets = ticketRepo.findByOperator(optionalUser.get());
            }

        }

        User user = optionalUser.get();
        model.addAttribute("user", user);

        model.addAttribute("tickets", tickets);

        return "ticket/index";
    }

    @GetMapping("/show/{id}")
    public String show(@PathVariable("id") Integer id, Model model) {
        // Recupera l'utente loggato dal contesto di sicurezza
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Recupera l'utente dal repository
        Optional<User> optionalUser = userRepo.findByUsername(username);
        if (!optionalUser.isPresent()) {
            throw new RuntimeException("User not found for username: " + username);
        }

        User user = optionalUser.get();
        model.addAttribute("user", user);

        Optional<Ticket> ticketOptional = ticketRepo.findById(id);
        if (ticketOptional.isPresent()) {
            model.addAttribute("tickets", ticketOptional.get());
        }

        return "/ticket/show";
    }

    // Metodo GET per visualizzare il form di create del ticket
    @GetMapping("/create")
    public String create(Model model) {

        // Recupera l'utente loggato dal contesto di sicurezza
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Recupera l'utente dal repository
        Optional<User> optionalUser = userRepo.findByUsername(username);
        if (!optionalUser.isPresent()) {
            throw new RuntimeException("User not found for username: " + username);
        }

        User user = optionalUser.get();
        model.addAttribute("user", user);

        // Inizializzo l'oggetto ticket
        model.addAttribute("ticket", new Ticket());
        model.addAttribute("allCategory", catRepo.findAll());
        model.addAttribute("availableOperator", userRepo.findByAvailable(true));
        model.addAttribute("states", State.values());
        return "/ticket/create";
    }

    // Metodo POST per gestire l'invio del form del ticket
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

    // Metodo GET per visualizzare il form di edit
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model) {

        // Recupera l'utente loggato dal contesto di sicurezza
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Recupera l'utente dal repository
        Optional<User> optionalUser = userRepo.findByUsername(username);
        if (!optionalUser.isPresent()) {
            throw new RuntimeException("User not found for username: " + username);
        }

        User user = optionalUser.get();
        model.addAttribute("user", user);

        model.addAttribute("newNote", new Note());
        model.addAttribute("ticket", ticketRepo.findById(id).get());
        model.addAttribute("allCategory", catRepo.findAll());
        model.addAttribute("availableOperator", userRepo.findByAvailable(true));
        model.addAttribute("states", State.values());

        return "/ticket/edit";
    }

    // Metodo POST per gestire il fomr della Edit
    @PostMapping("/edit/{id}")
    public String update(@Valid @ModelAttribute("ticket") Ticket formTicket,
            BindingResult bindingResult, Model model) {
        // Solito controllo per gli errori di validazine
        if (bindingResult.hasErrors()) {
            return "/ticket/edit";
        }

        ticketRepo.save(formTicket);

        return "redirect:/dashboard";
    }

    // Metdo POST per gestire la Delete
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id) {

        ticketRepo.deleteById(id);

        return "redirect:/dashboard";
    }

    // Metodo GET per le Note
    @GetMapping("/note/{id}")
    public String newNote(@PathVariable("id") Integer id, Model model) {

        // Recupera l'utente loggato dal contesto di sicurezza
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Recupera l'utente dal repository
        Optional<User> optionalUser = userRepo.findByUsername(username);
        if (!optionalUser.isPresent()) {
            throw new RuntimeException("User not found for username: " + username);
        }

        User user = optionalUser.get();
        model.addAttribute("user", user);

        // Recupera il ticket
        Ticket ticket = ticketRepo.findById(id).get();

        Note note = new Note();
        note.setTicket(ticket);

        model.addAttribute("ticketId", ticket.getId());
        model.addAttribute("note", note);

        return "note/create";
    }

}
