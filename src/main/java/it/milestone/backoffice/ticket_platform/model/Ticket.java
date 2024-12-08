package it.milestone.backoffice.ticket_platform.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "The title can't be null")
    @NotBlank(message = "The title can't be empty")
    @Size(max = 30, message = "The title can't be have max 30 characters")
    private String title;

    @NotNull(message = "The description can't be null")
    @NotBlank(message = "The description can't be empty")
    private String description;

    @NotNull(message = "The state can't be null")
    @Enumerated(EnumType.STRING)
    private State state;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_operator")
    private User operator;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_category")
    private Category category;

    private LocalDate date;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public User getOperator() {
        return operator;
    }

    public void setOperator(User operator) {
        this.operator = operator;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Ticket [id=" + id + ", title=" + title + ", description=" + description + ", state=" + state
                + ", operator=" + operator + ", category=" + category + ", date=" + date + "]";
    }

}
