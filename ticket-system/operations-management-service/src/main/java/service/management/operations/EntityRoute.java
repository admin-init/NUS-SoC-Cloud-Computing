package service.management.operations;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
public class EntityRoute extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    
    @ManyToOne
    @JoinColumn(name = "StartStationID")
    private EntityStation startStation;

    @ManyToOne
    @JoinColumn(name = "EndStationID")
    private EntityStation endStation;

    private Integer distance;

    public EntityStation getStartStation() {
        return startStation;
    }

    public void setStartStation(EntityStation startStation) {
        this.startStation = startStation;
    }

    public EntityStation getEndStation() {
        return endStation;
    }

    public void setEndStation(EntityStation endStation) {
        this.endStation = endStation;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }
}