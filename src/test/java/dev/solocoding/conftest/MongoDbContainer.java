package dev.solocoding.conftest;

import org.testcontainers.containers.GenericContainer;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.util.Map;


public class MongoDbContainer implements QuarkusTestResourceLifecycleManager {
    
    public static final String DEFAULT_IMAGE_AND_TAG = "mongo:bionic";
    public static final String MONGODB_HOST = "localhost";
    public static final int MONGODB_PORT = 27017;

    public static final GenericContainer<?> MONGO  = new GenericContainer<>(DEFAULT_IMAGE_AND_TAG)
                                                        .withExposedPorts(MONGODB_PORT);
    @Override
    public Map<String, String> start() {
        MONGO.start();
        return Map.of("quarkus.mongodb.connection-string", 
            "mongodb://".concat(MONGODB_HOST).concat(":").concat(MONGO.getMappedPort(MONGODB_PORT).toString())
            .concat("/testdb"));
    }
    
    @Override
    public void stop() {
        MONGO.stop();
    }


}
