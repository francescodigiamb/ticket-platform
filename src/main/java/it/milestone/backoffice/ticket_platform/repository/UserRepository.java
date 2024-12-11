package it.milestone.backoffice.ticket_platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import it.milestone.backoffice.ticket_platform.model.User;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUsername(String username);

    List<User> findByAvailable(boolean available);

}
