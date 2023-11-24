package org.johng.connectedcar.container.apis.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response;

import org.johng.connectedcar.container.apis.AdminResources;
import org.johng.connectedcar.core.shared.data.entities.Customer;
import org.johng.connectedcar.core.shared.data.updates.CustomerProvision;
import org.junit.jupiter.api.Test;

public class AdminCustomerResourceTest extends ResourceTest {

  private static final ResourceFixture fixture = new ResourceFixture();

  @Test
  public void testCreate() {
    CustomerProvision customer = getCustomerProvision();

    AdminResources endpoints = new AdminResources(fixture.getInjector());

    Response createResponse = endpoints.createCustomer(customer);

    String location = createResponse.getHeaderString("Location");
    String dealerId = parseLocation(location);

    assertEquals(201, createResponse.getStatus());
    assertNotNull(dealerId);
  }
  
  @Test
  public void testRetrieve() {
    CustomerProvision customer = getCustomerProvision();

    AdminResources resources = new AdminResources(fixture.getInjector());

    Response createResponse = resources.createCustomer(customer);

    String location = createResponse.getHeaderString("Location");
    String username = parseLocation(location);

    Response retrieveResponse = resources.getCustomer(username);

    assertSame(retrieveResponse.getEntity().getClass(), Customer.class);

    assertEquals(200, retrieveResponse.getStatus());
    assertNotNull(retrieveResponse.getEntity());

    Customer retrieved = (Customer)retrieveResponse.getEntity();

    assertEquals(customer.getFirstname(), retrieved.getFirstname());
  }

  @Test
  public void testList() {
    CustomerProvision customer = getCustomerProvision();

    AdminResources resources = new AdminResources(fixture.getInjector());

    Response createResponse = resources.createCustomer(customer);

    String location = createResponse.getHeaderString("Location");
    String username = parseLocation(location);

    Response listResponse = resources.getCustomers(customer.getLastname());

    assertEquals(200, listResponse.getStatus());
    assertNotNull(listResponse.getEntity());

    assertSame(ArrayList.class, listResponse.getEntity().getClass());

    @SuppressWarnings("unchecked")
    ArrayList<Customer> list = (ArrayList<Customer>)listResponse.getEntity();

    List<Customer> matches = list.stream()
      .filter(p -> p.getUsername().equals(username))
      .collect(Collectors.toList());

    assertNotEquals(0, matches.size());
    assertEquals(customer.getFirstname(), matches.get(0).getFirstname());
  }
}
