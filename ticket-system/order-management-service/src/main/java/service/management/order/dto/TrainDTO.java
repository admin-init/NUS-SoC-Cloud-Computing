package service.management.order.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class TrainDTO {

    private Long id;
    private Integer seats;

    public TrainDTO() {}

    public TrainDTO(Long id, Integer seats) {
        this.id = id;
        this.seats = seats;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSeats() {
        return seats;
    }

    public void setSeats(Integer seats) {
        this.seats = seats;
    }
}