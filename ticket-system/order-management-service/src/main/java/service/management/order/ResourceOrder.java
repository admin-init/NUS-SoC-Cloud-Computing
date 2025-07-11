package service.management.order;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/orders")
public class ResourceOrder {

    @Inject
    ServiceOrder orderService;

    @POST
    @Path("/search")
    public Response addOrder(EntityOrder order) {
        EntityOrder saved = orderService.addOrder(order);
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }
}