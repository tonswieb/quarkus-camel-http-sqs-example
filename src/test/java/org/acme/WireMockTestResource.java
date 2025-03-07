package org.acme;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class WireMockTestResource implements QuarkusTestResourceLifecycleManager {
    private static final int WIREMOCK_PORT = 8080;
    
    private GenericContainer<?> wiremockContainer;

    @Override
    public Map<String, String> start() {
        Map<String, String> properties = new HashMap<>();
        
        // Start WireMock Container
        wiremockContainer = new GenericContainer<>(DockerImageName.parse("wiremock/wiremock:3.4.2"))
                .withExposedPorts(WIREMOCK_PORT)
                .withClasspathResourceMapping("wiremock", "/home/wiremock", BindMode.READ_ONLY)
                .waitingFor(Wait.forHttp("/__admin/mappings").forStatusCode(200))
                .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("wiremock")));
        
        wiremockContainer.start();
        
        String wiremockHost = wiremockContainer.getHost();
        Integer wiremockPort = wiremockContainer.getMappedPort(WIREMOCK_PORT);

        // Set properties for the test
        properties.put("order.polling.url", wiremockHost + ":" + wiremockPort + "/orders");

        return properties;
    }

    @Override
    public void stop() {
        if (wiremockContainer != null) {
            wiremockContainer.stop();
        }
    }

    public GenericContainer<?> getWiremockContainer() {
        return wiremockContainer;
    }
} 