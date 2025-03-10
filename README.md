# getting-started

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it's not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/getting-started-1.0.0-SNAPSHOT-runner`

By default, the runner will try to connect to localstack on localhost:4566, so if you want that to work, first [install the localstack CLI](https://docs.localstack.cloud/getting-started/installation/), then start it:

```shell script
localstack start
```

You can observe messages being sent to SQS using the convenient [`awscli-local` tool](https://github.com/localstack/awscli-local) (If you already have `uv`, quickly install it using `uv tool install awscli-local`):

```shell
while true; do
    awslocal sqs receive-message --queue-url order-processing-queue --max-number-of-messages 10 --wait-time-seconds 20;
done
```

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Related Guides

- Camel Core ([guide](https://camel.apache.org/camel-quarkus/latest/reference/extensions/core.html)): Camel core functionality and basic Camel languages: Constant, ExchangeProperty, Header, Ref, Simple and Tokenize
- Camel AWS 2 SQS ([guide](https://camel.apache.org/camel-quarkus/latest/reference/extensions/aws2-sqs.html)): Send and receive messages to/from AWS SQS service using AWS SDK version 2.x
- Camel HTTP ([guide](https://camel.apache.org/camel-quarkus/latest/reference/extensions/http.html)): Send requests to HTTP endpoints
- Camel XSLT ([guide](https://camel.apache.org/camel-quarkus/latest/reference/extensions/xslt.html)): Transform XML payloads using XSLT stylesheets
- Camel XML Validation ([guide](https://camel.apache.org/camel-quarkus/latest/reference/extensions/xml-validator.html)): Validate XML payloads using XML Schema
- Quarkus CDI ([guide](https://quarkus.io/guides/cdi-reference)): Write CDI applications with Quarkus
- Quarkus Configuration ([guide](https://quarkus.io/guides/config-reference)): Configure your application using MicroProfile Config
- Quarkus Test Framework ([guide](https://quarkus.io/guides/getting-started-testing)): Write tests for your Quarkus application
- TestContainers ([guide](https://quarkus.io/guides/dev-services)): Spin up Docker containers for your tests
- WireMock ([guide](https://wiremock.org/docs/getting-started/)): Mock HTTP-based APIs for testing

## Project Description

This project demonstrates a Camel route that:
1. Polls an HTTP endpoint for XML orders
2. Validates the input XML against an XSD schema
3. Transforms the XML using XSLT
4. Validates the transformed XML against another XSD schema
5. Sends the processed order to an AWS SQS queue

The project includes comprehensive tests using:
- WireMock for HTTP endpoint mocking
- LocalStack for AWS SQS service emulation
- TestContainers for managing test dependencies
- Quarkus Test Framework for integration testing
