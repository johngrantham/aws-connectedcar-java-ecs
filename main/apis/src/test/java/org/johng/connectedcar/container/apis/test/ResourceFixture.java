package org.johng.connectedcar.container.apis.test;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class ResourceFixture {
  
  private Injector injector;

  public ResourceFixture() {
	  injector = Guice.createInjector(new TestModule());
  }

  public Injector getInjector() {
    return injector;
  }
}
