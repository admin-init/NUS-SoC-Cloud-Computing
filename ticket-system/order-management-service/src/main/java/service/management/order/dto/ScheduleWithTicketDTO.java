// src/main/java/com/example/order/dto/
package service.management.order.dto;

import java.time.LocalDateTime;

public class ScheduleWithTicketDTO {
    public Long scheduleId;
    public LocalDateTime departureTime;
    public LocalDateTime arrivalTime;
    public String startStation;
    public String endStation;
    public Integer availableTickets;
}