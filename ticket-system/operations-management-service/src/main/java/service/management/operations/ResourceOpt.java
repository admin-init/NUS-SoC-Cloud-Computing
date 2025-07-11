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
// @RolesAllowed("ADMIN")
public class ResourceOpt {

    @Inject
    ServiceAdmin adminService;

    @Inject
    ServiceSearch searchService;

    @Inject
    ServiceTicket serviceTicket;

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
        serviceTicket.generateTicketsForSchedules();
        return Response.ok("Ticket Generated Successfully").build();
    }

    @GET
    @Path("/tickets/available")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkAvailableTickets(@QueryParam("scheduleId") Long scheduleId) {
        Integer available = serviceTicket.getAvailableTickets(scheduleId);
        return Response.ok().entity("{\"available\": " + available + "}").build();
    }

    @POST
    @Path("/tickets/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchTicket(@QueryParam("ticketId") Long ticketId) {
        EntityTicket found = serviceTicket.searchTicket(ticketId);
        return Response.status(Response.Status.CREATED).entity(found).build();
    }

    @POST
    @Path("/tickets/reduce")
    @Produces(MediaType.TEXT_PLAIN)
    public Response reduceTicketAvailability(@QueryParam("ticketId") Long ticketId) {
        serviceTicket.reduceTicketAvailability(ticketId);
        return Response.ok("TicketAvailability Reduced Successfully").build();
    }

}