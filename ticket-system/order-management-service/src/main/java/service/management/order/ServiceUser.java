package service.management.order;


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