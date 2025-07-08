// src/main/java/com/example/ticketmanagement/TicketService.java

package com.example.ticketmanagement;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.listAll();
    }

    public Optional<Ticket> getTicketById(Long id) {
        return ticketRepository.findByIdOptional(id);
    }

    public void createTicket(Ticket ticket) {
        ticketRepository.persist(ticket);
    }

    public boolean updateTicket(Long id, Ticket newTicketData) {
        return ticketRepository.findByIdOptional(id).map(ticket -> {
            ticket.title = newTicketData.title;
            ticket.description = newTicketData.description;
            ticket.status = newTicketData.status;
            return true;
        }).orElse(false);
    }

    public boolean deleteTicket(Long id) {
        return ticketRepository.deleteById(id);
    }
}
