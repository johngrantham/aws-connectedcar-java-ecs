package org.johng.connectedcar.container.apis;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;

import com.google.inject.Injector;
import org.johng.connectedcar.core.shared.data.entities.Appointment;
import org.johng.connectedcar.core.shared.data.entities.Customer;
import org.johng.connectedcar.core.shared.data.entities.Dealer;
import org.johng.connectedcar.core.shared.data.entities.Event;
import org.johng.connectedcar.core.shared.data.entities.Registration;
import org.johng.connectedcar.core.shared.data.entities.Timeslot;
import org.johng.connectedcar.core.shared.data.entities.Vehicle;
import org.johng.connectedcar.core.shared.data.enums.StateCodeEnum;
import org.johng.connectedcar.core.shared.data.updates.CustomerPatch;

@Path("/customer")
public class CustomerResources extends BaseResources {

	public CustomerResources() {}

  public CustomerResources(Injector injector) {
		super(injector);
  }
  
	@PATCH
	@Path("/profile")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateCustomer(@HeaderParam("authorization") String authorization, CustomerPatch customerPatch) {
		return process(() -> {
			validate("authorization", authorization);
			validate(customerPatch);
			String username = getUsername(authorization);

			customerPatch.setUsername(username);
			getCustomerService().updateCustomer(customerPatch);

  		return Response.ok().build();
		});
	}

	@GET
	@Path("/profile")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCustomer(@HeaderParam("authorization") String authorization) {
		return process(() -> {
			validate("authorization", authorization);
			String username = getUsername(authorization);
			
      Customer customer = getCustomerService().getCustomer(username);

			if (customer != null)
				return Response.ok(customer).build();
			else
  			return Response.status(404).build();
		});
	} 

	/******************************************************************************************/

	@POST
	@Path("/appointments")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createAppointment(@HeaderParam("authorization") String authorization, Appointment appointment) {
		return process(() -> {
			validate("authorization", authorization);
			validate(appointment);
			String username = getUsername(authorization);

      String appointmentId = getCustomerOrchestrator().createAppointment(username, appointment);
      
      if (appointmentId != null) {
  			return Response.created(URI.create("/customer/appointments/" + appointmentId)).build();
      }
  		
			return Response.status(400).build();
		});
	}

	@DELETE
	@Path("/appointments/{appointmentId}")
	public Response deleteAppointment(@HeaderParam("authorization") String authorization, @PathParam("appointmentId") String appointmentId) {
		return process(() -> {
			validate("authorization", authorization);
			validate("appointmentId", appointmentId);
			String username = getUsername(authorization);

      Appointment appointment = getAppointmentService().getAppointment(appointmentId);
      
      if (appointment != null && appointment.getRegistrationKey().getUsername().equals(username)) {
        getAppointmentService().deleteAppointment(appointmentId);
  			return Response.ok().build();
      }

  		return Response.status(400).build();
		});
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/appointments/{appointmentId}")
	public Response getAppointment(@HeaderParam("authorization") String authorization, @PathParam("appointmentId") String appointmentId) {
		return process(() -> {
			validate("authorization", authorization);
			validate("appointmentId", appointmentId);
			String username = getUsername(authorization);

      Appointment appointment = getAppointmentService().getAppointment(appointmentId);
      
      if (appointment != null && appointment.getRegistrationKey().getUsername().equals(username)) {
  			return Response.ok(appointment).build();
      }

  		return Response.status(404).build();
		});
	}

	/******************************************************************************************/
  
	@GET
	@Path("/registrations")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCustomerRegistrations(@HeaderParam("authorization") String authorization) {
		return process(() -> {
			validate("authorization", authorization);
			String username = getUsername(authorization);

      List<Registration> registrations = getRegistrationService().getCustomerRegistrations(username);

			return Response.ok(registrations).build();
		});
	} 

	@GET
	@Path("/registrations/{vin}/appointments")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAppointments(@HeaderParam("authorization") String authorization, @PathParam("vin") String vin) {
		return process(() -> {
			validate("authorization", authorization);
      validate("vin", vin);
			String username = getUsername(authorization);

      List<Appointment> appointments = getAppointmentService().getRegistrationAppointments(username, vin);

			return Response.ok(appointments).build();
		});
	} 

	/******************************************************************************************/

	@GET
	@Path("/vehicles/{vin}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getVehicle(@HeaderParam("authorization") String authorization, @PathParam("vin") String vin) {
		return process(() -> {
			validate("authorization", authorization);
      validate("vin", vin);
			String username = getUsername(authorization);

      Vehicle vehicle = getCustomerOrchestrator().getVehicle(username, vin);

			if (vehicle != null) {
				return Response.ok(vehicle).build();
			}

  		return Response.status(404).build();
		});
	} 

	@GET
	@Path("/vehicles/{vin}/events")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEvents(@HeaderParam("authorization") String authorization, @PathParam("vin") String vin) {
		return process(() -> {
			validate("authorization", authorization);
      validate("vin", vin);
			String username = getUsername(authorization);

      List<Event> events = getCustomerOrchestrator().getEvents(username, vin);

			return Response.ok(events).build();
		});
	} 

	/******************************************************************************************/

	@GET
	@Path("/dealers")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDealers(@QueryParam("stateCode") String code) {
		return process(() -> {
			validate("stateCode", code);
			StateCodeEnum stateCode = StateCodeEnum.valueOf(code);
			List<Dealer> dealers = getDealerService().getDealers(stateCode);

			return Response.ok(dealers).build();
		});
	} 

	@GET
	@Path("/dealers/{dealerId}/timeslots")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTimeslots(@PathParam("dealerId") String dealerId) {
		return process(() -> {
			validate("dealerId", dealerId);

      final String DATE_FORMAT = "yyyy-MM-dd";
      SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);

      Date today = new Date();
      Calendar calendar = Calendar.getInstance(); 
      calendar.setTime(today);
      calendar.add(Calendar.DATE, 30);
      Date future = calendar.getTime();

      String startDate = formatter.format(today);
      String endDate = formatter.format(future);

      List<Timeslot> timeslots = getTimeslotService().getTimeslots(dealerId, startDate, endDate);

			return Response.ok(timeslots).build();
		});
	} 

	/******************************************************************************************/

	private String getUsername(String authorization) throws InvalidJwtException {
		String jwt = authorization.substring(7);
		JwtClaims claims = getUnvalidatedClaims(jwt);
		return claims.getClaimValueAsString("username");
	}
	
  private JwtClaims getUnvalidatedClaims(String jwt) throws InvalidJwtException {
    JwtConsumer jwtConsumer = new JwtConsumerBuilder()
      .setSkipSignatureVerification()
      .setSkipAllValidators()
      .build();

      return jwtConsumer.processToClaims(jwt);
  }
}
