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
import org.johng.connectedcar.core.shared.data.entities.Dealer;
import org.junit.jupiter.api.Test;

public class AdminDealerResourceTest extends ResourceTest {

  private static final ResourceFixture fixture = new ResourceFixture();

  @Test
  public void testCreate() {
    Dealer dealer = getDealer();

    AdminResources resources = new AdminResources(fixture.getInjector());

    Response createResponse = resources.createDealer(dealer);

    String location = createResponse.getHeaderString("Location");
    String dealerId = parseLocation(location);

    assertEquals(201, createResponse.getStatus());
    assertNotNull(dealerId);
  }
  
  @Test
  public void testRetrieve() {
    Dealer dealer = getDealer();

    AdminResources resources = new AdminResources(fixture.getInjector());

    Response createResponse = resources.createDealer(dealer);

    String location = createResponse.getHeaderString("Location");
    String dealerId = parseLocation(location);

    Response retrieveResponse = resources.getDealer(dealerId);

    assertEquals(200, retrieveResponse.getStatus());
    assertNotNull(retrieveResponse.getEntity());

    assertSame(retrieveResponse.getEntity().getClass(), Dealer.class);

    Dealer retrieved = (Dealer)retrieveResponse.getEntity();

    assertEquals(dealer.getName(), retrieved.getName());
  }

  @Test
  public void testList() {
    Dealer dealer = getDealer();

    AdminResources resources = new AdminResources(fixture.getInjector());

    Response createResponse = resources.createDealer(dealer);

    String location = createResponse.getHeaderString("Location");
    String dealerId = parseLocation(location);

    Response listResponse = resources.getDealers(dealer.getStateCode().toString());

    assertEquals(200, listResponse.getStatus());
    assertNotNull(listResponse.getEntity());

    assertSame(ArrayList.class, listResponse.getEntity().getClass());

    @SuppressWarnings("unchecked")
    ArrayList<Dealer> list = (ArrayList<Dealer>)listResponse.getEntity();

    List<Dealer> matches = list.stream()
      .filter(p -> p.getDealerId().equals(dealerId))
      .collect(Collectors.toList());

    assertNotEquals(0, matches.size());
    assertEquals(dealer.getName(), matches.get(0).getName());
  }
}
