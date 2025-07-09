package service.management.operations;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
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
@RolesAllowed("ADMIN")
public class AdminResource {

    @POST
    @Path("/stations")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addStation(EntityStation station) {
        station.persist();
        return Response.ok(station).build();
    }

    @POST
    @Path("/trains")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addTrain(EntityTrain train) {
        train.persist();
        return Response.ok(train).build();
    }

    @POST
    @Path("/routes")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addRoute(EntityRoute route) {
        route.persist();
        return Response.ok(route).build();
    }

    @POST
    @Path("/schedules")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addSchedule(EntitySchedule schedule) {
        schedule.persist();
        return Response.ok(schedule).build();
    }
}