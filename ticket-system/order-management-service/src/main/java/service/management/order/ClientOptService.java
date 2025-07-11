package service.management.order;

import service.management.order.dto.TicketDTO;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "operations-api")
public interface ClientOptService {

    @POST
    @Path("/admin/tickets/search")
    @Produces(MediaType.APPLICATION_JSON)
    TicketDTO searchTicket(@QueryParam("ticketId") Long ticketId);

    @POST
    @Path("/admin/tickets/reduce")
    @Produces(MediaType.TEXT_PLAIN)
    Response reduceTicketAvailability(@QueryParam("ticketId") Long ticketId);
}