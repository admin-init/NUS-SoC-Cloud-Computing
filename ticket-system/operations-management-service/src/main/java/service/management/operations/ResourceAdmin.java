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

@Path("/admin")
public class ResourceAdmin {

    @Inject
    ServiceAdmin adminService;

    @Inject
    ServiceTicket ticketService;

    @POST
    @Path("/stations/add")
    public Response addStation(EntityStation station) {
        EntityStation saved = adminService.addStation(station);
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }

    @POST
    @Path("/trains/add")
    public Response addTrain(EntityTrain train) {
        EntityTrain saved = adminService.addTrain(train);
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }

    @POST
    @Path("/routes/add")
    public Response addRoute(EntityRoute route) {
        EntityRoute saved = adminService.addRoute(route);
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }

    @POST
    @Path("/schedules/add") 
    public Response addSchedule(EntitySchedule schedule) {
        EntitySchedule saved = adminService.addSchedule(schedule);
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }

    @POST
    @Path("/tickets/add")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addTicket(EntityTicket ticket) {
        EntityTicket saved = adminService.addTicket(ticket);
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }

    @POST
    @Path("/tickets/generate")
    @Produces(MediaType.TEXT_PLAIN)
    public Response generateTickets() {
        ticketService.generateTicketsForSchedules();
        return Response.ok("Ticket Generated Successfully").build();
    }

    @POST
    @Path("/tickets/reduce")
    @Produces(MediaType.TEXT_PLAIN)
    public Response reduceTicketAvailability(@QueryParam("ticketId") Long ticketId) {
        ticketService.reduceTicketAvailability(ticketId);
        return Response.ok("TicketAvailability Reduced Successfully").build();
    }

}