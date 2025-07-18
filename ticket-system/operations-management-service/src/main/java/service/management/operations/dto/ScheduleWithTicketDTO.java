// src/main/java/com/example/order/dto/
package service.management.operations.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import java.time.LocalDateTime;

@RegisterForReflection
public class ScheduleWithTicketDTO {
    public Long scheduleId;
    public Long ticketId;
    public LocalDateTime departureTime;
    public LocalDateTime arrivalTime;
    public String startStation;
    public String endStation;
    public Integer availableTickets;
}