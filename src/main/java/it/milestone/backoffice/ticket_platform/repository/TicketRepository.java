package it.milestone.backoffice.ticket_platform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.milestone.backoffice.ticket_platform.model.State;
import it.milestone.backoffice.ticket_platform.model.Ticket;
import it.milestone.backoffice.ticket_platform.model.User;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    List<Ticket> findByTitleContainingIgnoreCase(String text);

    List<Ticket> findByOperatorId(Integer operatorId);

    List<Ticket> findByOperator(User operator);

    List<Ticket> findByOperatorUsernameAndTitleContainingIgnoreCase(String username, String text);

    List<Ticket> findByCategory_Name(String name);

    List<Ticket> findByState(State state);

}
