// src/man/java/com/example/ticketmanagement/TicketRepository.java

package com.example.ticketmanagement;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TicketRepository implements PanacheRepository<Ticket> {
}
