package service.management.order;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RepositoryOrder implements PanacheRepository<EntityOrder> {
}