package it.milestone.backoffice.ticket_platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.milestone.backoffice.ticket_platform.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);

    List<User> findByAvailable(boolean available);

}
