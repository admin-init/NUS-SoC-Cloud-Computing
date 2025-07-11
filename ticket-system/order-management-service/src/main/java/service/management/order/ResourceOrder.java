package service.management.order;

// DTOs
import service.management.order.dto.ScheduleDTO;
import service.management.order.dto.TicketDTO;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;


@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ResourceOrder {

    @Inject
    ServiceOrder orderService;

    /**
     * 创建新订单
     */
    @POST
    @Path("/{userId}/create")
    @Produces(MediaType.TEXT_PLAIN)
    public Response createOrder(
            @PathParam("userId") Long userId, 
            @QueryParam("ticketId") Long ticketId) {
        orderService.createOrder(userId, ticketId);
        return Response.ok("Order Created Successfully").build();
    }

    /**
     * 根据用户ID和订单ID查询订单详情
     */
    @GET
    @Path("/{userId}/view/{orderId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrderById(@PathParam("userId") Long userId, @PathParam("orderId") Long orderId) {
        EntityOrder order = orderService.getOrderByIdAndUserId(orderId, userId);
        if (order == null) {
            return Response.status(Response.Status.NOT_FOUND)
                        .entity("Order not found or does not belong to the user")
                        .build();
        }
        return Response.ok(order).build();
    }

    /**
     * 获取Current User所有订单列表
     */
    @GET
    @Path("/{userId}/view")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrdersByUserId(@PathParam("userId") Long userId) {
        List<EntityOrder> orders = orderService.getOrdersByUserId(userId);
        if (orders == null || orders.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                        .entity("No orders found for this user")
                        .build();
        }
        return Response.ok(orders).build();
    }

}