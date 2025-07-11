package service.management.user;

/*
import model.EntityStation;
import model.EntityTrain;
import model.EntityRoute;
import model.EntitySchedule;
import repository.StationRepository;
import repository.TrainRepository;
import repository.RouteRepository;
import repository.ScheduleRepository;
*/

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.inject.Inject;

@ApplicationScoped
@Transactional
public class ServiceUser {

    @Inject
    RepositoryUser userRepo;

    @Inject
    RepositoryAdmin adminRepo;

    public EntityUser addUser(EntityUser user) {
        userRepo.persist(user);
        return user;
    }

    public EntityAdmin addAdmin(EntityAdmin admin) {
        adminRepo.persist(admin);
        return admin;
    }
}