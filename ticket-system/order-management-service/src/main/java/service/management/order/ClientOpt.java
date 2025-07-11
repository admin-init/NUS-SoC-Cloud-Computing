package service.management.order;

import service.management.order.dto.ScheduleDTO;
import service.management.order.dto.TicketDTO;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@RegisterRestClient(configKey = "operations-api")
@Produces(MediaType.APPLICATION_JSON)
public interface ClientOpt {

    @GET
    @Path("/search")
    List<ScheduleDTO> findAvailableSchedules(
        @QueryParam("date") String date,
        @QueryParam("startStationId") Integer startStationId,
        @QueryParam("endStationId") Integer endStationId);

    List<TicketDTO> getTicketsBySchedule(Integer scheduleId);
}

