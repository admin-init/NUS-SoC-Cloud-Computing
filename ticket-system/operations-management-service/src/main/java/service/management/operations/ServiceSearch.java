package service.management.operations;

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


    public List<EntitySchedule> findAvailableSchedules(LocalDateTime date, Long startStationId, Long endStationId) {
        // 可以在这里加逻辑判断余票是否充足等
        return scheduleRepo.findByDateAndStations(date, startStationId, endStationId);
    }
}