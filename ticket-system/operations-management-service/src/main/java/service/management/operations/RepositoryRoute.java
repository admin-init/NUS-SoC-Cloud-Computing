package service.management.operations;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RepositoryRoute implements PanacheRepository<EntityRoute> {
}