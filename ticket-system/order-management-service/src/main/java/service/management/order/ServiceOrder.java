package service.management.order;

import service.management.order.dto.ScheduleDTO;
import service.management.order.dto.TicketDTO;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.inject.Inject;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.GenericType;

import com.fasterxml.jackson.databind.JsonNode;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@ApplicationScoped
@Transactional
public class ServiceOrder {

    @Inject
    RepositoryOrder orderRepo;

    public EntityOrder addOrder(EntityOrder order) {
        orderRepo.persist(order);
        return order;
    }

    public EntityOrder getOrderByIdAndUserId(Long orderId, Long userId) {
        return orderRepo.findByIdAndUserId(orderId, userId);
    }

    public List<EntityOrder> getOrdersByUserId(Long userId) {
        return orderRepo.findByUserId(userId);
    }

    @Inject
    @RestClient
    ClientOptService optServiceClient;

    public EntityOrder createOrder(Long userId, Long ticketId) {
        EntityOrder createdOrder = new EntityOrder();

        TicketDTO ticketDTO = optServiceClient.searchTicket(ticketId);
        if (ticketDTO == null || ticketDTO.getSchedule() == null) {
            throw new WebApplicationException("Ticket or schedule not found", Response.Status.NOT_FOUND);
        }

        createdOrder.setUserId(userId);
        createdOrder.setTicketId(ticketId);
        createdOrder.setScheduleId(ticketDTO.getSchedule().getId());
        createdOrder.setStatus("Finished");
        createdOrder.setPrice(ticketDTO.getPrice());

        addOrder(createdOrder);

        optServiceClient.reduceTicketAvailability(ticketId);

        return createdOrder;
    }
}