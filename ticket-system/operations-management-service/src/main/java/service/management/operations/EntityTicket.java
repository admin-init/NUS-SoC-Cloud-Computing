package service.management.operations;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
public class EntityTicket extends PanacheEntity {

    @ManyToOne
    @JoinColumn(name = "ScheduleID")
    private EntitySchedule schedule;

    private Integer amount;         
    private Integer availableAmount; 
    private Integer price;          

    public EntitySchedule getSchedule() {
        return schedule;
    }

    public void setSchedule(EntitySchedule schedule) {
        this.schedule = schedule;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(Integer availableAmount) {
        this.availableAmount = availableAmount;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}