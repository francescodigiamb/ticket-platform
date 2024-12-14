package it.milestone.backoffice.ticket_platform.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.milestone.backoffice.ticket_platform.model.State;
import it.milestone.backoffice.ticket_platform.model.Ticket;
import it.milestone.backoffice.ticket_platform.model.User;
import it.milestone.backoffice.ticket_platform.repository.TicketRepository;
import it.milestone.backoffice.ticket_platform.repository.UserRepository;

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

        // Verifica se ci sono ticket in stato "TO_DO" o "IN_PROGRESS"
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
        model.addAttribute("isDisabled", hasActiveTickets); // Flag per disabilitare il pulsante

        return "user/index"; // Ritorna alla vista della pagina dettagli utente
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

        // Verifica se ci sono ticket in stato "TO_DO" o "IN_PROGRESS"
        boolean hasActiveTickets = false;
        for (Ticket ticket : assignedTickets) {
            if (ticket.getState() == State.TO_DO || ticket.getState() == State.IN_PROGRESS) {
                hasActiveTickets = true;
                break;
            }
        }

        if (hasActiveTickets) {
            // Aggiunge un messaggio di errore al modello
            model.addAttribute("errorMessage", "Non puoi impostare lo stato su 'non attivo' se hai ticket attivi.");
            model.addAttribute("user", user);
            model.addAttribute("isDisabled", true);
            return "user/index"; // Ritorna alla pagina di profilo utente con l'errore
        }

        // Se non ha ticket attivi, cambia lo stato dell'utente
        user.setAvailable(status);
        userRepo.save(user);

        return "redirect:/user/details/" + id; // Reindirizza alla pagina dei dettagli utente
    }

}
