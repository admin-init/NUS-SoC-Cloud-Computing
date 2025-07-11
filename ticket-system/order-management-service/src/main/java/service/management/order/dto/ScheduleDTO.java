// src/main/java/com/example/order/dto/
package service.management.order.dto;

import java.time.LocalDate;

public class ScheduleDTO {

    private Long id;
    private String scheduleCode;
    private RouteDTO route;
    private TrainDTO train;
    private LocalDate departureTime;
    private LocalDate arrivalTime;

    // Constructors, Getters and Setters

    public ScheduleDTO() {}

    public ScheduleDTO(Long id, String scheduleCode, RouteDTO route, TrainDTO train,
                       LocalDate departureTime, LocalDate arrivalTime) {
        this.id = id;
        this.scheduleCode = scheduleCode;
        this.route = route;
        this.train = train;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getScheduleCode() {
        return scheduleCode;
    }

    public void setScheduleCode(String scheduleCode) {
        this.scheduleCode = scheduleCode;
    }

    public RouteDTO getRoute() {
        return route;
    }

    public void setRoute(RouteDTO route) {
        this.route = route;
    }

    public TrainDTO getTrain() {
        return train;
    }

    public void setTrain(TrainDTO train) {
        this.train = train;
    }

    public LocalDate getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDate departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDate getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDate arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
}