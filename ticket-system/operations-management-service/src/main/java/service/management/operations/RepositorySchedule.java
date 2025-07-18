package service.management.operations;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@ApplicationScoped
public class RepositorySchedule implements PanacheRepository<EntitySchedule> {

    public List<EntitySchedule> findByDateAndStations(LocalDateTime departureTime, Long startStationId, Long endStationId) {
        LocalDateTime startTime = departureTime.with(LocalTime.MIN); // 2025-07-12 00:00:00
        LocalDateTime endTime = departureTime.with(LocalTime.MAX);   // 2025-07-12 23:59:59

            return list("departureTime between ?1 and ?2 " +
                            "and route.startStation.id = ?3 " +
                            "and route.endStation.id = ?4",
                    startTime, endTime, startStationId, endStationId);
        }
}