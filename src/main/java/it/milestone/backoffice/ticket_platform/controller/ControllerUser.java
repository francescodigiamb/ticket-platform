package it.milestone.backoffice.ticket_platform.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.milestone.backoffice.ticket_platform.model.Note;
import it.milestone.backoffice.ticket_platform.model.State;
import it.milestone.backoffice.ticket_platform.model.Ticket;
import it.milestone.backoffice.ticket_platform.model.User;

import it.milestone.backoffice.ticket_platform.repository.TicketRepository;
import it.milestone.backoffice.ticket_platform.repository.UserRepository;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class ControllerUser {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private TicketRepository ticketRepo;

    @GetMapping("/details/{id}")
    public String showUserDetails(@PathVariable("id") Integer id, Model model) {
        // Recupera l'utente dal repository
        User user = userRepo.findById(id).orElse(null);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Recupera i ticket assegnati all'utente
        List<Ticket> assignedTickets = ticketRepo.findByOperator(user);

        // Verifica lo stato dei ticket
        boolean hasActiveTickets = false;
        for (Ticket ticket : assignedTickets) {
            if (ticket.getState() == State.TO_DO || ticket.getState() == State.IN_PROGRESS) {
                hasActiveTickets = true;
                break;
            }
        }

        // Passa i dati al modello
        model.addAttribute("user", user);
        model.addAttribute("tickets", assignedTickets);
        model.addAttribute("isDisabled", hasActiveTickets);

        return "user/index";
    }

    @PostMapping("/update/{id}")
    public String updateUserStatus(@PathVariable("id") Integer id, @RequestParam("status") boolean status,
            Model model) {
        // Recupera l'utente dal database
        User user = userRepo.findById(id).orElse(null);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Recupera i ticket assegnati all'utente
        List<Ticket> assignedTickets = ticketRepo.findByOperator(user);

        // Verifica lo stato dei ticket
        boolean hasActiveTickets = false;
        for (Ticket ticket : assignedTickets) {
            if (ticket.getState() == State.TO_DO || ticket.getState() == State.IN_PROGRESS) {
                hasActiveTickets = true;
                break;
            }
        }

        if (hasActiveTickets) {
            // Aggiunge un messaggio di errore al modello
            model.addAttribute("errorMessage",
                    "Non puoi impostare lo stato su 'non attivo' se hai ticket attivi.");
            model.addAttribute("user", user);
            model.addAttribute("isDisabled", true);
            return "user/index";
        }

        // Se non ha ticket attivi, cambia lo stato dell'utente
        user.setAvailable(status);
        userRepo.save(user);

        return "redirect:/user/details/" + id;
    }

    @GetMapping("/editstate/{id}")
    public String edit(@PathVariable("id") Integer id, Model model) {

        model.addAttribute("ticket", ticketRepo.findById(id).get());
        model.addAttribute("states", State.values());

        return "/user/edit";
    }

    @PostMapping("/editstate/{id}")
    public String update(@PathVariable("id") Integer id,
            @RequestParam("state") State state, Model model) {
        // Recupera il ticket esistente dal database
        Ticket ticket = ticketRepo.findById(id).orElse(null);

        if (ticket == null) {
            throw new IllegalArgumentException("Ticket not found");
        }

        ticket.setState(state);

        ticketRepo.save(ticket);

        return "redirect:/dashboard";
    }
    // @GetMapping("/create")
    // public String create(Model model) {

    // // Recupera l'utente loggato dal contesto di sicurezza
    // Authentication authentication =
    // SecurityContextHolder.getContext().getAuthentication();
    // String username = authentication.getName();

    // // Recupera l'utente dal repository
    // Optional<User> optionalUser = userRepo.findByUsername(username);
    // if (!optionalUser.isPresent()) {
    // throw new RuntimeException("User not found for username: " + username);
    // }

    // User user = optionalUser.get();
    // model.addAttribute("user", user);

    // // Inizializza l'oggetto ticket
    // model.addAttribute("user", new User());

    // return "/user/create";
    // }

    // @PostMapping("/create")
    // public String store(@Valid @ModelAttribute("user") User formUser,
    // BindingResult bindingResult, Model model) {

    // // Se ci sono errori di validazione, torna al form
    // if (bindingResult.hasErrors()) {

    // return "/user/create";
    // }
    // // Salva l'user nel database
    // userRepo.save(formUser);
    // // Reindirizza alla dashboard
    // return "redirect:/dashboard";
    // }

}
