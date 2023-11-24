package org.johng.connectedcar.container.apis;

import java.net.URI;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class GrizzlyLauncher {
	
	public static final String BASE_URI = "http://0.0.0.0:8080";
	
    private static HttpServer server;

    public static void main(String[] args)
    {
        System.out.println("Starting server");
        server = startServer();
        System.out.println("server starting, continuing forever");
        Runtime.getRuntime().addShutdownHook(new ShutdownThread());
        for(;;){}
    }

	public static HttpServer startServer() {
        ResourceConfig config = new ResourceConfig();

        config.packages("org.johng.connectedcar.core.shared.data.attributes", "org.johng.connectedcar.core.shared.data.entities");
        config.register(getProvider());
        
        config.register(AdminResources.class);
        config.register(CustomerResources.class);
        config.register(VehicleResources.class);

        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), config);

        return server;      
    }

    public static class ShutdownThread extends Thread {
        public void run() {
            server.shutdownNow();
        }
    }

    private static JacksonJsonProvider getProvider() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        JacksonJsonProvider provider = new JacksonJsonProvider();
        provider.setMapper(mapper);

        return provider;
    }
}
