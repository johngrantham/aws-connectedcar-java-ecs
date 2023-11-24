package org.johng.connectedcar.container.apis.test;

import com.google.inject.Singleton;
import com.google.inject.AbstractModule;
import org.johng.connectedcar.core.shared.orchestrators.AdminOrchestrator;
import org.johng.connectedcar.core.shared.orchestrators.IAdminOrchestrator;
import org.johng.connectedcar.core.shared.orchestrators.ICustomerOrchestrator;
import org.johng.connectedcar.core.shared.orchestrators.CustomerOrchestrator;
import org.johng.connectedcar.core.shared.services.IAppointmentService;
import org.johng.connectedcar.core.shared.services.ICustomerService;
import org.johng.connectedcar.core.shared.services.IDealerService;
import org.johng.connectedcar.core.shared.services.IEventService;
import org.johng.connectedcar.core.shared.services.IMessageService;
import org.johng.connectedcar.core.shared.services.IRegistrationService;
import org.johng.connectedcar.core.shared.services.ITimeslotService;
import org.johng.connectedcar.core.shared.services.IUserService;
import org.johng.connectedcar.core.shared.services.IVehicleService;
import org.johng.connectedcar.core.test.services.MockAppointmentService;
import org.johng.connectedcar.core.test.services.MockCustomerService;
import org.johng.connectedcar.core.test.services.MockDealerService;
import org.johng.connectedcar.core.test.services.MockEventService;
import org.johng.connectedcar.core.test.services.MockRegistrationService;
import org.johng.connectedcar.core.test.services.MockTimeslotService;
import org.johng.connectedcar.core.test.services.MockVehicleService;
import org.johng.connectedcar.core.test.services.StubMessageService;
import org.johng.connectedcar.core.test.services.StubUserService;

public class TestModule extends AbstractModule {

  @Override 
  protected void configure() {
    bind(IDealerService.class).to(MockDealerService.class).in(Singleton.class);
    bind(ITimeslotService.class).to(MockTimeslotService.class).in(Singleton.class);
    bind(IAppointmentService.class).to(MockAppointmentService.class).in(Singleton.class);
    bind(ICustomerService.class).to(MockCustomerService.class).in(Singleton.class);
    bind(IRegistrationService.class).to(MockRegistrationService.class).in(Singleton.class);
    bind(IVehicleService.class).to(MockVehicleService.class).in(Singleton.class);
    bind(IEventService.class).to(MockEventService.class).in(Singleton.class);
    bind(IUserService.class).to(StubUserService.class).in(Singleton.class);
    bind(IMessageService.class).to(StubMessageService.class).in(Singleton.class);
    bind(IAdminOrchestrator.class).to(AdminOrchestrator.class).in(Singleton.class);
    bind(ICustomerOrchestrator.class).to(CustomerOrchestrator.class).in(Singleton.class);
  }
}
