package it.milestone.backoffice.ticket_platform.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.milestone.backoffice.ticket_platform.model.State;
import it.milestone.backoffice.ticket_platform.model.Ticket;
import it.milestone.backoffice.ticket_platform.repository.TicketRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@CrossOrigin
@RequestMapping("/api/dashboard")
public class TicketRestController {

    @Autowired
    private TicketRepository ticketRepo;

    @GetMapping
    public ResponseEntity<List<Ticket>> index(@RequestParam(name = "keyword", required = false) String keyword) {

        try {
            if (keyword != null && !keyword.isBlank()) {
                return new ResponseEntity<List<Ticket>>(ticketRepo.findByTitleContainingIgnoreCase(keyword),
                        HttpStatus.OK);
            } else {
                return ResponseEntity.ok(ticketRepo.findAll());
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }

    // Filtrare i ticket per categoria
    @GetMapping("/category/{name}")
    public ResponseEntity<List<Ticket>> getTicketsByCategory(@PathVariable String name) {

        try {
            return ResponseEntity.ok(ticketRepo.findByCategory_Name(name));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Filtrare i ticket per stato
    @GetMapping("/state/{state}")
    public ResponseEntity<List<Ticket>> getTicketsByState(@PathVariable State state) {
        try {
            return ResponseEntity.ok(ticketRepo.findByState(state));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }

}
