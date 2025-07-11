package service.management.order.dto;

public class RouteDTO {

    private Long id;
    private StationDTO startStation;
    private StationDTO endStation;
    private Integer distance;

    public RouteDTO() {}

    public RouteDTO(Long id, StationDTO startStation, StationDTO endStation, Integer distance) {
        this.id = id;
        this.startStation = startStation;
        this.endStation = endStation;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StationDTO getStartStation() {
        return startStation;
    }

    public void setStartStation(StationDTO startStation) {
        this.startStation = startStation;
    }

    public StationDTO getEndStation() {
        return endStation;
    }

    public void setEndStation(StationDTO endStation) {
        this.endStation = endStation;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }
}