package it.milestone.backoffice.ticket_platform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.milestone.backoffice.ticket_platform.model.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    List<Ticket> findByTitleContainingIgnoreCase(String text);

}
