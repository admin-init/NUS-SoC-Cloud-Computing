package service.management.order;

import service.management.order.dto.ScheduleDTO;
import service.management.order.dto.TicketDTO;
import service.management.order.dto.ScheduleWithTicketDTO;

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

    public List<ScheduleWithTicketDTO> searchSchedulesWithTickets(
            String departureTimeString, Long startStationId, Long endStationId) {

        Response response = optServiceClient.findAvailableSchedules(
                departureTimeString, startStationId, endStationId);

        if (response.getStatus() == 200) {
            List<ScheduleDTO> schedules = response.readEntity(new GenericType<List<ScheduleDTO>>() {});

            return schedules.stream()
                    .map(schedule -> {
                        ScheduleWithTicketDTO dto = new ScheduleWithTicketDTO();

                        // 使用 getter 方法获取字段值
                        dto.scheduleId = schedule.getId();
                        dto.departureTime = schedule.getDepartureTime();
                        dto.arrivalTime = schedule.getArrivalTime();
                        dto.startStation = schedule.getRoute().getStartStation().getName();  // 从 RouteDTO 获取
                        dto.endStation = schedule.getRoute().getEndStation().getName();      // 从 RouteDTO 获取

                        try {
                            Response ticketResponse = optServiceClient.checkAvailableTickets(dto.scheduleId);

                            if (ticketResponse.getStatus() == 200) {
                                JsonNode jsonNode = ticketResponse.readEntity(JsonNode.class);
                                dto.availableTickets = jsonNode.get("available").asInt();
                            } else {
                                dto.availableTickets = -1; // 查询失败
                            }
                        } catch (Exception e) {
                            System.err.println("Error fetching available tickets for schedule ID: " + dto.scheduleId);
                            e.printStackTrace(); // 打印异常堆栈
                            dto.availableTickets = -2; // 异常处理
                        }

                        return dto;
                    })
                    .toList();

        } else {
            System.out.println("Failed to fetch schedules: " + response.readEntity(String.class));
            return List.of();
        }
    }
}