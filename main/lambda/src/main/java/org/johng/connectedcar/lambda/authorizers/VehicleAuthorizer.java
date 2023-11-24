package org.johng.connectedcar.lambda.authorizers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.johng.connectedcar.core.services.modules.NonTracingModule;
import org.johng.connectedcar.core.shared.authpolicy.AuthPolicy;
import org.johng.connectedcar.core.shared.authpolicy.AuthPolicyFactory;
import org.johng.connectedcar.core.shared.services.IVehicleService;

public class VehicleAuthorizer implements RequestStreamHandler {

  private static final String HEADER_X_VIN = "X-Vin";
  private static final String HEADER_X_PIN = "X-Pin";

  private Injector injector;
  private ObjectMapper objectMapper;

  public VehicleAuthorizer() {
    injector = Guice.createInjector(new NonTracingModule());
    objectMapper = new ObjectMapper();
  }

  @Override
  public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
    JsonNode root = objectMapper.readTree(inputStream);

    String vin = root.path("headers").path(HEADER_X_VIN).asText();
    String pin = root.path("headers").path(HEADER_X_PIN).asText();

    boolean isAllowed = false;
    
    if (!StringUtils.isNullOrEmpty(vin) && !StringUtils.isNullOrEmpty(pin)) {
      isAllowed = getVehicleService().validatePin(vin, pin);
    }

    AuthPolicy policy = AuthPolicyFactory.GetApiPolicy(
      vin, 
      isAllowed);

      objectMapper.writeValue(outputStream, policy);
  }

  private IVehicleService getVehicleService() {
    return injector.getInstance(IVehicleService.class);
  }
}
