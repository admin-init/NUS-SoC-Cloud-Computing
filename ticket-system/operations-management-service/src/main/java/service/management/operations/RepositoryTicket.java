package service.management.operations;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class RepositoryTicket implements PanacheRepository<EntityTicket> {

    // public EntityTicket findById(Long id);

    /**
     * 使用悲观锁查询票信息
     */
    public EntityTicket findByIdWithLock(Long id) {
        return find("id = ?1", id)
                .withLock(LockModeType.PESSIMISTIC_WRITE)
                .firstResultOptional()
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found for ID: " + id));
    }

    public boolean existsBySchedule(Long scheduleId) {
        return count("schedule.id = ?1", scheduleId) > 0;
    }

    public EntityTicket findBySchedule(Long scheduleId) {
        return find("schedule.id = ?1", scheduleId).firstResult();
    }
}