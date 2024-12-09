package it.milestone.backoffice.ticket_platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.milestone.backoffice.ticket_platform.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

}
