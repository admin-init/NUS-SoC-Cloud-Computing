package service.management.operations;

import service.management.operations.dto.ScheduleWithTicketDTO;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/*
*    @Path("/hello")
*    public class GreetingResource {
*
*        @GET
*        @Produces(MediaType.TEXT_PLAIN)
*        public String hello() {
*            return "Hello from Quarkus REST";
*        }
*    }
*/

@Path("/opt")
public class ResourceOpt {

    @Inject
    ServiceSearch searchService;

    @Inject
    ServiceTicket ticketService;

    @GET
    @Path("/schedules/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAvailableSchedules(
            @QueryParam("departureTime") String departureTimeString,
            @QueryParam("startStationId") Long startStationId,
            @QueryParam("endStationId") Long endStationId) {

        try {
            LocalDateTime date;

            if (departureTimeString.contains(" ")) {
                DateTimeFormatter fullFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                date = LocalDateTime.parse(departureTimeString, fullFormatter);
            } else {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate localDate = LocalDate.parse(departureTimeString, dateFormatter);
                date = localDate.atStartOfDay();
            }

            List<ScheduleWithTicketDTO> schedulesWITHtickets = searchService.findAvailableSchedules(date, startStationId, endStationId);

            return Response.ok(schedulesWITHtickets).build();

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid date format. Please use yyyy-MM-dd [HH:mm:ss]")
                    .build();
        }
    }

    @POST
    @Path("/tickets/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchTicket(@QueryParam("ticketId") Long ticketId) {
        EntityTicket found = ticketService.searchTicket(ticketId);
        return Response.status(Response.Status.CREATED).entity(found).build();
    }

    @GET
    @Path("/tickets/available")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkAvailableTickets(@QueryParam("scheduleId") Long scheduleId) {
        Integer available = ticketService.getAvailableTickets(scheduleId);
        return Response.ok().entity("{\"available\": " + available + "}").build();
    }
}