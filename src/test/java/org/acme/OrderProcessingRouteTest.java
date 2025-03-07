package org.acme;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;

@QuarkusTest
@QuarkusTestResource(LocalStackTestResource.class)
@QuarkusTestResource(WireMockTestResource.class)
public class OrderProcessingRouteTest {

    private static final String QUEUE_NAME = "test-queue";

    @ConfigProperty (name = "camel.component.aws2-sqs.uri-endpoint-override")
    String endpointOverride;
    @ConfigProperty (name = "camel.component.aws2-sqs.access-key")
    String accessKey;
    @ConfigProperty (name = "camel.component.aws2-sqs.secret-key")
    String secretKey;
    @ConfigProperty (name = "camel.component.aws2-sqs.region")
    String region;

    @Test
    void testOrderProcessingRoute() throws Exception {
        // Wait for the route to process
        Thread.sleep(15000);

        SqsClient sqsClient = SqsClient.builder()
            .endpointOverride(URI.create(endpointOverride))
            .credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                            accessKey,
                            secretKey)))
            .region(Region.of(region))
            .build();

        
        // Get the queue URL
        String queueUrl = sqsClient.getQueueUrl(GetQueueUrlRequest.builder()
                .queueName(QUEUE_NAME)
                .build())
                .queueUrl();

        // Check SQS message
        var receiveMessageResponse = sqsClient.receiveMessage(
                ReceiveMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .waitTimeSeconds(20)
                        .build());

        assertFalse(receiveMessageResponse.messages().isEmpty(), "No messages received from SQS");
        
        String receivedMessage = receiveMessageResponse.messages().get(0).body();
        
        // Verify the message content
        assertTrue(receivedMessage.contains("<processedOrder>"), "Message should contain processedOrder");
        assertTrue(receivedMessage.contains("<orderReference>ORD-2024-001</orderReference>"), "Message should contain correct order reference");
        assertTrue(receivedMessage.contains("<totalOrderValue>109.97</totalOrderValue>"), "Message should contain correct total value");
    }
} 