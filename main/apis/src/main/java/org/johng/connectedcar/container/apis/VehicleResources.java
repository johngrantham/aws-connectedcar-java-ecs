package org.johng.connectedcar.container.apis;

import java.net.URI;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.inject.Injector;
import org.johng.connectedcar.core.shared.data.entities.Event;


@Path("/vehicle")
public class VehicleResources extends BaseResources {
  
	public VehicleResources() {}

  public VehicleResources(Injector injector) {
		super(injector);
  }

	@POST
	@Path("/events")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createEvent(@HeaderParam("X-Vin") String vin, Event event) {
		return process(() -> {
      validate("vin", vin);
			validate(event);

			event.setVin(vin);
			getEventService().createEvent(event);
			return Response.created(URI.create("/vehicle/events/" + event.getTimestamp())).build();
		});
	}

	@GET
	@Path("/events")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEvents(@HeaderParam("X-Vin") String vin) {
		return process(() -> {
      validate("vin", vin);
			
      List<Event> events = getEventService().getEvents(vin);

			return Response.ok(events).build();
		});
	} 

	@GET
	@Path("/events/{timestamp}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEvent(@HeaderParam("X-Vin") String vin, @PathParam("timestamp") String timestamp) {
		return process(() -> {
      validate("vin", vin);
			validate("timestamp", timestamp);

      Event event = getEventService().getEvent(vin, Long.parseLong(timestamp));

			if (event != null)
				return Response.ok(event).build();
			else
  			return Response.status(404).build();
		});
	} 
}
