package service.management.operations;

import service.management.operations.dto.ScheduleWithTicketDTO;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.inject.Inject;

import java.util.List;
import java.time.LocalDateTime;


@ApplicationScoped
@Transactional
public class ServiceSearch {

    @Inject
    RepositoryStation stationRepo;

    @Inject
    RepositoryTrain trainRepo;

    @Inject
    RepositoryRoute routeRepo;

    @Inject
    RepositorySchedule scheduleRepo;

    @Inject
    RepositoryTicket ticketRepo;


    public List<ScheduleWithTicketDTO> findAvailableSchedules(LocalDateTime date, Long startStationId, Long endStationId) {
        List<EntitySchedule> schedules = scheduleRepo.findByDateAndStations(date, startStationId, endStationId);

        return schedules.stream()
                    .map(schedule -> {
                        ScheduleWithTicketDTO dto = new ScheduleWithTicketDTO();

                        // 使用 getter 方法获取字段值
                        dto.scheduleId = schedule.getId();
                        dto.departureTime = schedule.getDepartureTime();
                        dto.arrivalTime = schedule.getArrivalTime();
                        dto.startStation = schedule.getRoute().getStartStation().getName();  // 从 RouteDTO 获取
                        dto.endStation = schedule.getRoute().getEndStation().getName();      // 从 RouteDTO 获取

                        EntityTicket ticket = ticketRepo.findBySchedule(schedule.getId());
                        int available = ticket != null ? ticket.getAvailableAmount() : 0;
                        dto.availableTickets = available;
                        dto.ticketId = ticket.getId();

                        return dto;
                    })
                    .toList();
    }

}