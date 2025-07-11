package service.management.user;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/hello")
public class GreetingResource {

    @Inject
    ServiceUser userService;

    @POST
    @Path("/users")
    public Response addUser(EntityUser user) {
        EntityUser saved = userService.addUser(user);
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }

    @POST
    @Path("/admins")
    public Response addAdmin(EntityAdmin admin) {
        EntityAdmin saved = userService.addAdmin(admin);
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }
}
