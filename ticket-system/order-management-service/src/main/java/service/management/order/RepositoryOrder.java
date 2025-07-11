package service.management.order;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class RepositoryOrder implements PanacheRepository<EntityOrder> {
    /**
     * 根据订单ID和用户ID查询订单
     */
    public EntityOrder findByIdAndUserId(Long id, Long userId) {
        return find("id = ?1 AND userId = ?2", id, userId).firstResult();
    }

    /**
     * 根据用户ID查询所有订单
     */
    public List<EntityOrder> findByUserId(Long userId) {
        return list("userId", userId);
    }
}