// src/main/java/com/example/ticketmanagement/TicketResource.java

package com.example.ticketmanagement;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Path("/tickets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TicketResource {

    @Inject
    TicketService ticketService;

    @GET
    public List<Ticket> getAllTickets() {
        return ticketService.getAllTickets();
    }

    @GET
    @Path("/{id}")
    public Response getTicketById(@PathParam("id") Long id) {
        return ticketService.getTicketById(id)
                .map(ticket -> Response.ok(ticket))
                .orElse(Response.status(Response.Status.NOT_FOUND))
                .build();
    }

    @POST
    public Response createTicket(Ticket ticket) {
        ticketService.createTicket(ticket);
        return Response.created(URI.create("/tickets/" + ticket.id)).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateTicket(@PathParam("id") Long id, Ticket newTicketData) {
        boolean updated = ticketService.updateTicket(id, newTicketData);
        if (!updated) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteTicket(@PathParam("id") Long id) {
        boolean deleted = ticketService.deleteTicket(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}
