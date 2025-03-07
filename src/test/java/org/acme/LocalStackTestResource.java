package org.acme;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

public class LocalStackTestResource implements QuarkusTestResourceLifecycleManager {
    
    private LocalStackContainer localstack;

    @Override
    public Map<String, String> start() {
        Map<String, String> properties = new HashMap<>();
        
        // Start LocalStack
        localstack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
                .withServices(SQS)
                .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("localstack")));
        localstack.start();
        
        // Configure AWS/SQS properties for Camel
        properties.put("aws.sqs.queue", "test-queue");
        properties.put("camel.component.aws2-sqs.access-key", localstack.getAccessKey());
        properties.put("camel.component.aws2-sqs.secret-key", localstack.getSecretKey());
        properties.put("camel.component.aws2-sqs.region", localstack.getRegion());
        // Use the endpoint override from LocalStack directly
        properties.put("camel.component.aws2-sqs.uri-endpoint-override", localstack.getEndpointOverride(SQS).toString());
        properties.put("camel.component.aws2-sqs.overrideEndpoint", "true");
        
        return properties;
    }

    @Override
    public void stop() {
        if (localstack != null) {
            localstack.stop();
        }
    }
} 