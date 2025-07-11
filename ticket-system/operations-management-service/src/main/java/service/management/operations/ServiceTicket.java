package service.management.operations;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
@Transactional
public class ServiceTicket {

    @Inject
    RepositorySchedule scheduleRepo;

    @Inject
    RepositoryTicket ticketRepo;

    /**
     * 根据所有已有的时刻表生成车票
     */
    public void generateTicketsForSchedules() {
        List<EntitySchedule> schedules = scheduleRepo.findAll().list();

        for (EntitySchedule schedule : schedules) {
            // 避免重复生成
            if (ticketRepo.existsBySchedule(schedule.id)) {
                continue;
            }

            EntityTicket ticket = new EntityTicket();

            // 设置票价
            Integer price = calculatePriceBasedOnDistance(schedule);

            // 获取列车座位数作为总票数
            int totalSeats = schedule.getTrain().getSeats();
            ticket.setAmount(totalSeats);
            ticket.setAvailableAmount(totalSeats); // 初始可用票数等于总票数

            ticket.setPrice(price);
            ticket.setSchedule(schedule); // 关联班次

            ticketRepo.persist(ticket); // 保存到数据库
        }

        System.out.println("✅ Generated " + schedules.size() + "schedules' tickets");
    }

    /**
     * 根据线路距离计算票价（每公里5元）
     */
    private Integer calculatePriceBasedOnDistance(EntitySchedule schedule) {
        if (schedule.getRoute() != null && schedule.getRoute().getDistance() != null) {
            return schedule.getRoute().getDistance() * 5; // 每公里 5 元
        }
        return 100; // 默认票价
    }

    /**
     * 查询某班次的剩余票数
     */
    public Integer getAvailableTickets(Long scheduleId) {
        EntityTicket ticket = ticketRepo.findBySchedule(scheduleId);
        return ticket != null ? ticket.getAvailableAmount() : 0;
    }

    /**
     * 扣减票数（模拟购票）
     */
    public boolean reduceTicketAvailability(Long scheduleId, int quantity) {
        EntityTicket ticket = ticketRepo.findBySchedule(scheduleId);
        if (ticket == null || ticket.getAvailableAmount() < quantity) {
            return false; // 无票或票不够
        }

        ticket.setAvailableAmount(ticket.getAvailableAmount() - quantity);
        ticketRepo.getEntityManager().merge(ticket);
        return true;
    }
}