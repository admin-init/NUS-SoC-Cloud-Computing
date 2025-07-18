// src/main/java/com/example/order/dto/
package service.management.order.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class TicketDTO {

    private Long id;
    private ScheduleDTO schedule;
    private Integer amount;
    private Integer availableAmount;
    private Double price;

    // Constructors, Getters and Setters

    public TicketDTO() {}

    public TicketDTO(Long id, ScheduleDTO schedule, Integer amount, Integer availableAmount, Double price) {
        this.id = id;
        this.schedule = schedule;
        this.amount = amount;
        this.availableAmount = availableAmount;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ScheduleDTO getSchedule() {
        return schedule;
    }

    public void setSchedule(ScheduleDTO schedule) {
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}