// src/man/java/com/example/ticketmanagement/Ticket.java

package com.example.ticketmanagement;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Ticket extends PanacheEntity {
    public String title;
    public String description;
    public String status; // e.g., OPEN, IN_PROGRESS, CLOSED
}
