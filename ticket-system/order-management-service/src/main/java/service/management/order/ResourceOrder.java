package service.management.order;

// DTOs
import service.management.order.dto.ScheduleDTO;
import service.management.order.dto.TicketDTO;
import service.management.order.dto.ScheduleWithTicketDTO;

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
    @Path("/create")
    public Response createOrder(EntityOrder order) {
        EntityOrder savedOrder = orderService.addOrder(order);
        return Response.status(Response.Status.CREATED).entity(savedOrder).build();
    }

    /**
     * 根据用户ID和订单ID查询订单详情
     */
    @GET
    @Path("/view/{userId}/{id}")
    public Response getOrderById(@PathParam("userId") Long userId, @PathParam("id") Long id) {
        EntityOrder order = orderService.getOrderByIdAndUserId(id, userId);
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
    @Path("/user/{userId}")
    public Response getOrdersByUserId(@PathParam("userId") Long userId) {
        List<EntityOrder> orders = orderService.getOrdersByUserId(userId);
        if (orders == null || orders.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                        .entity("No orders found for this user")
                        .build();
        }
        return Response.ok(orders).build();
    }

    /**
     * 搜索可用车次安排（调用 Opt 服务）
     */
    @GET
    @Path("/search")
    public Response searchSchedules(
            @QueryParam("departureTime") String departureTimeString,
            @QueryParam("startStationId") Long startStationId,
            @QueryParam("endStationId") Long endStationId) {

        try {
            List<ScheduleWithTicketDTO> schedules = orderService.searchSchedulesWithTickets(
                    departureTimeString, startStationId, endStationId);

            if (schedules.isEmpty()) {
                return Response.status(Response.Status.NO_CONTENT).build();
            }

            return Response.ok(schedules).build();

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error fetching schedules: " + e.getMessage())
                    .build();
        }
    }
}