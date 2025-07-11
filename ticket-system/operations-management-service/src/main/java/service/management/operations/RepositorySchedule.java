package service.management.operations;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class RepositorySchedule implements PanacheRepository<EntitySchedule> {
    public List<EntitySchedule> findByDateAndStations(String date, Integer startStationId, Integer endStationId) {
        return list("DepartureTime like ?1 and StartStationID = ?2 and EndStationID = ?3", 
                    "%" + date + "%", startStationId, endStationId);
    }
}