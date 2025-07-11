package service.management.operations;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class RepositoryTicket implements PanacheRepository<EntityTicket> {

    public List<EntityTicket> findByScheduleId(Long scheduleId) {
        return EntityTicket.list("ScheduleID", scheduleId);
    }

    // public EntityTicket findById(Long id);

    public boolean existsBySchedule(Long scheduleId) {
        return count("schedule.id = ?1", scheduleId) > 0;
    }

    public EntityTicket findBySchedule(Long scheduleId) {
        return find("schedule.id = ?1", scheduleId).firstResult();
    }
}