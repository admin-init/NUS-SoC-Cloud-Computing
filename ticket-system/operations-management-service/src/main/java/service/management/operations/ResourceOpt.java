package service.management.operations;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
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

    @POST
    @Path("/tickets/add")
    public Response addTicket(EntityTicket ticket) {
        EntityTicket saved = adminService.addTicket(ticket);
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public List<EntitySchedule> findAvailableSchedules(
        @QueryParam("date") String date,
        @QueryParam("startStationId") Integer startStationId,
        @QueryParam("endStationId") Integer endStationId) {

        return searchService.findAvailableSchedules(date, startStationId, endStationId);
    }

    @POST
    @Path("/tickets/generate")
    @Produces(MediaType.TEXT_PLAIN)
    public Response generateTickets() {
        serviceTicket.generateTicketsForSchedules();
        return Response.ok("车票生成完成！").build();
    }

    @GET
    @Path("/tickets/available")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkAvailableTickets(@QueryParam("scheduleId") Long scheduleId) {
        Integer available = serviceTicket.getAvailableTickets(scheduleId);
        return Response.ok().entity("{\"available\": " + available + "}").build();
    }
}