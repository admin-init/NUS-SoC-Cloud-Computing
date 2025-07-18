package service.management.operations;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.inject.Inject;

@ApplicationScoped
@Transactional
public class ServiceAdmin {

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

    public EntityStation addStation(EntityStation station) {
        stationRepo.persist(station);
        return station;
    }

    public EntityTrain addTrain(EntityTrain train) {
        trainRepo.persist(train);
        return train;
    }

    public EntityRoute addRoute(EntityRoute route) {
        routeRepo.persist(route);
        return route;
    }

    public EntitySchedule addSchedule(EntitySchedule schedule) {
        scheduleRepo.persist(schedule);
        return schedule;
    }

    public EntityTicket addTicket(EntityTicket ticket) {
        ticketRepo.persist(ticket);
        return ticket;
    }

/*
    public void generateTicketsForSchedule(EntitySchedule schedule) {
        // Step 1: Retrieve related data
        EntityRoute route = routeRepo.findById(schedule.getRouteID()).orElseThrow(() -> new RuntimeException("Route not found"));
        EntityTrain train = trainRepo.findById(schedule.getTrainID()).orElseThrow(() -> new RuntimeException("Train not found"));

        // Step 2: Calculate price based on distance
        int distance = route.getDistance();
        int pricePerSeat = calculatePricePerSeat(distance);

        // Step 3: Create and save tickets for each seat
        for (int i = 0; i < seats; i++) {
            EntityTicket ticket = new EntityTicket();
            ticket.setScheduleID(schedule.getID());
            ticket.setPrice(pricePerSeat);
            ticketRepo.persist(ticket);
        }
    }

    private int calculatePricePerSeat(int distance) {
        // price per kilometer
        return distance; // $1 per km
    }
*/
}