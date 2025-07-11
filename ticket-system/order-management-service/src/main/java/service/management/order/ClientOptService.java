package service.management.order;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "operations-api")
public interface ClientOptService {

    @GET
    @Path("/admin/schedules/search")
    @Produces("application/json")
    Response findAvailableSchedules(
            @QueryParam("departureTime") String departureTimeString,
            @QueryParam("startStationId") Long startStationId,
            @QueryParam("endStationId") Long endStationId);

    @GET
    @Path("/admin/tickets/available")
    @Produces("application/json")
    Response checkAvailableTickets(@QueryParam("scheduleId") Long scheduleId);

    @POST
    @Path("/admin/tickets/reduce")
    @Produces(MediaType.TEXT_PLAIN)
    Response reduceTicketAvailability(@QueryParam("scheduleId") Long scheduleId);
}