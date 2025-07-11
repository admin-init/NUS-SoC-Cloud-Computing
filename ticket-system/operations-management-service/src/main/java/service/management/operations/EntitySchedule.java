package service.management.operations;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import java.time.LocalDate;

@Entity
public class EntitySchedule extends PanacheEntity {

    private String scheduleCode;

    @ManyToOne
    @JoinColumn(name = "RouteID")
    private EntityRoute route;

    @ManyToOne
    @JoinColumn(name = "TrainID")
    private EntityTrain train;

    private LocalDate departureTime;
    private LocalDate arrivalTime;

    public String getScheduleCode() {
        return scheduleCode;
    }

    public void setScheduleCode(String scheduleCode) {
        this.scheduleCode = scheduleCode;
    }

    public EntityRoute getRoute() {
        return route;
    }

    public void setRoute(EntityRoute route) {
        this.route = route;
    }

    public EntityTrain getTrain() {
        return train;
    }

    public void setTrain(EntityTrain train) {
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