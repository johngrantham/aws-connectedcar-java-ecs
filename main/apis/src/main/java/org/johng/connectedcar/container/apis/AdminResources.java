package org.johng.connectedcar.container.apis;

import java.net.URI;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.inject.Injector;
import org.johng.connectedcar.core.shared.data.entities.Customer;
import org.johng.connectedcar.core.shared.data.entities.Dealer;
import org.johng.connectedcar.core.shared.data.entities.Registration;
import org.johng.connectedcar.core.shared.data.entities.Timeslot;
import org.johng.connectedcar.core.shared.data.entities.Vehicle;
import org.johng.connectedcar.core.shared.data.enums.StateCodeEnum;
import org.johng.connectedcar.core.shared.data.updates.CustomerProvision;
import org.johng.connectedcar.core.shared.data.updates.RegistrationPatch;

@Path("/admin")
public class AdminResources extends BaseResources {

	public AdminResources() {}

  public AdminResources(Injector injector) {
		super(injector);
  }

	@POST
	@Path("/dealers")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createDealer(Dealer dealer) {
		return process(() -> {
			validate(dealer);
			String dealerId = getDealerService().createDealer(dealer);

  		return Response.created(URI.create("/admin/dealers/" + dealerId)).build();
		});
	}

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
	@Path("/dealers/{dealerId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDealer(@PathParam("dealerId") String dealerId) {
		return process(() -> {
			validate("dealerId", dealerId);
			Dealer dealer = getDealerService().getDealer(dealerId);

			if (dealer != null)
				return Response.ok(dealer).build();
			else
  			return Response.status(404).build();
		});
	} 

	/******************************************************************************************/

	@POST
	@Path("/dealers/{dealerId}/timeslots")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createTimeslot(Timeslot timeslot) {
		return process(() -> {
			validate(timeslot);
			getTimeslotService().createTimeslot(timeslot);

  		return Response.created(URI.create("/admin/dealers/" + timeslot.getDealerId() + "/timeslots/" + timeslot.getServiceDateHour())).build();
		});
	}

	@GET
	@Path("/dealers/{dealerId}/timeslots")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTimeslots(@PathParam("dealerId") String dealerId, @QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate) {
		return process(() -> {
			validate("dealerId", dealerId);
			validate("startDate", startDate);
			validate("endDate", endDate);

			List<Timeslot> timeslots = getTimeslotService().getTimeslots(dealerId, startDate, endDate);

			return Response.ok(timeslots).build();
		});
	} 

	@GET
	@Path("/dealers/{dealerId}/timeslots/{serviceDateHour}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTimeslot(@PathParam("dealerId") String dealerId, @PathParam("serviceDateHour") String serviceDateHour) {
		return process(() -> {
			validate("dealerId", dealerId);
			validate("serviceDateHour", serviceDateHour);
      Timeslot timeslot = getTimeslotService().getTimeslot(dealerId, serviceDateHour);

			if (timeslot != null)
				return Response.ok(timeslot).build();
			else
  			return Response.status(404).build();
		});
	} 

	/******************************************************************************************/

	@POST
	@Path("/customers")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createCustomer(CustomerProvision provision) {
		return process(() -> {
			validate(provision);

			getAdminOrchestrator().createCustomerUsingMessage(provision);

  		return Response.created(URI.create("/admin/customers/" + provision.getUsername())).build();
		});
	}

	@GET
	@Path("/customers")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCustomers(@QueryParam("lastname") String lastname) {
		return process(() -> {
			validate("lastname", lastname);
      List<Customer> customers = getCustomerService().getCustomers(lastname);

			return Response.ok(customers).build();
		});
	} 

	@GET
	@Path("/customers/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCustomer(@PathParam("username") String username) {
		return process(() -> {
			validate("username", username);
      Customer customer = getCustomerService().getCustomer(username);

			if (customer != null)
				return Response.ok(customer).build();
			else
  			return Response.status(404).build();
		});
	} 

	/******************************************************************************************/

	@POST
	@Path("/customers/{username}/registrations")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createRegistration(Registration registration) {
		return process(() -> {
			validate(registration);
			getRegistrationService().createRegistration(registration);

  		return Response.created(URI.create("/admin/customers/" + registration.getUsername() + "/registrations/" + registration.getVin())).build();
		});
	}

	@PATCH
	@Path("/customers/{username}/registrations")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateRegistration(RegistrationPatch patch) {
		return process(() -> {
			validate(patch);
			getRegistrationService().updateRegistration(patch);

  		return Response.ok().build();
		});
	}

	@GET
	@Path("/customers/{username}/registrations")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCustomerRegistrations(@PathParam("username") String username) {
		return process(() -> {
			validate("username", username);
      List<Registration> registrations = getRegistrationService().getCustomerRegistrations(username);

			return Response.ok(registrations).build();
		});
	} 

	@GET
	@Path("/customers/{username}/registrations/{vin}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRegistration(@PathParam("username") String username, @PathParam("vin") String vin) {
		return process(() -> {
			validate("username", username);
			validate("vin", vin);

      Registration registration = getRegistrationService().getRegistration(username, vin);

			if (registration != null)
				return Response.ok(registration).build();
			else
  			return Response.status(404).build();
		});
	} 

	/******************************************************************************************/

	@POST
	@Path("/vehicles")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createVehicle(Vehicle vehicle) {
		return process(() -> {
			validate(vehicle);
			getVehicleService().createVehicle(vehicle);

  		return Response.created(URI.create("/admin/vehicles/" + vehicle.getVin())).build();
		});
	}

	@GET
	@Path("/vehicles/{vin}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getVehicle(@PathParam("vin") String vin) {
		return process(() -> {
			validate("vin", vin);
      Vehicle vehicle = getVehicleService().getVehicle(vin);

			if (vehicle != null)
				return Response.ok(vehicle).build();
			else
  			return Response.status(404).build();
		});
	} 

	@GET
	@Path("/vehicles/{vin}/registrations")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getVehicleRegistrations(@PathParam("vin") String vin) {
		return process(() -> {
			validate("vin", vin);
      List<Registration> registrations = getRegistrationService().getVehicleRegistrations(vin);

			return Response.ok(registrations).build();
		});
	} 
}
