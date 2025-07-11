package service.management.order;

import service.management.order.dto.ScheduleDTO;
import service.management.order.dto.TicketDTO;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@ApplicationScoped
@Transactional
public class ServiceOrder {

    @Inject
    RepositoryOrder orderRepo;

    public EntityOrder addOrder(EntityOrder order) {
        orderRepo.persist(order);
        return order;
    }

    @Inject
    @RestClient
    ClientOpt optClient;

    public EntityOrder createOrder(Integer userId, Integer scheduleId) {
        // 1. 获取余票信息
        List<TicketDTO> tickets = optClient.getTicketsBySchedule(scheduleId);

        if (tickets == null || tickets.isEmpty()) {
            throw new RuntimeException("No tickets available for schedule: " + scheduleId);
        }

        // 2. 假设我们取第一张票的价格作为参考
        Integer price = tickets.get(0).getPrice();

        // 3. 创建订单
        EntityOrder order = new EntityOrder();
        order.setUserId(userId);
        order.setScheduleId(scheduleId);
        order.setPrice(price);
        order.setStatus("pending");

        orderRepo.persist(order);

        // TODO: 如果需要，可以发送消息通知 Operations Service 扣减库存

        return order;
    }
}

