package it.milestone.backoffice.ticket_platform.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import it.milestone.backoffice.ticket_platform.model.Note;
import it.milestone.backoffice.ticket_platform.model.Ticket;
import it.milestone.backoffice.ticket_platform.repository.NoteRepository;
import it.milestone.backoffice.ticket_platform.repository.TicketRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/note")
public class ControllerNote {

    @Autowired
    private NoteRepository noteRepo;

    @Autowired
    private TicketRepository ticketRepo;

    @PostMapping("/create")
    public String store(@Valid @ModelAttribute("note") Note note, BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "note/create";
        }

        // Recupera il ticket usando l'ID
        Ticket ticket = ticketRepo.findById(note.getTicket().getId()).orElse(null);
        if (ticket == null) {
            throw new RuntimeException("Ticket not found");
        }

        // Associa il ticket alla nota
        note.setTicket(ticket);
        note.setCreationDate(LocalDate.now());

        noteRepo.save(note);

        return "redirect:/dashboard/show/" + note.getTicket().getId();
    }

    @PostMapping("/delete/{id}")
    public String deleteNote(@PathVariable("id") Integer id) {

        noteRepo.deleteById(id);

        return "redirect:/dashboard";
    }

}
