package org.johng.connectedcar.lambda.events;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.johng.connectedcar.core.services.modules.NonTracingModule;
import org.johng.connectedcar.core.shared.data.User;
import org.johng.connectedcar.core.shared.services.IUserService;

public class CreateUserEventHandler implements RequestHandler<SQSEvent, Void> {

  private Injector injector;
  private ObjectMapper objectMapper;

  public CreateUserEventHandler() {
    injector = Guice.createInjector(new NonTracingModule());
    objectMapper = new ObjectMapper();
  }

  @Override
  public Void handleRequest(SQSEvent event, Context context) {
    try {
      for (SQSMessage msg : event.getRecords()) {
        User user = objectMapper.readValue(msg.getBody(), User.class);
        getUserService().createUser(user);
      }
    }
		catch (Exception e) {
      StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      context.getLogger().log(sw.toString());
		}

    return null;
  }

  private IUserService getUserService() {
    return injector.getInstance(IUserService.class);
  }
}
