package com.example.kafka.connector;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

public class KafkaSourceConfig extends AbstractConfig {
    // Define configuration keys
    public static final String SOURCE_BOOTSTRAP_SERVERS = "source.bootstrap.servers";
    public static final String SOURCE_TOPIC = "source.topic";
    public static final String TARGET_TOPIC = "target.topic";

    // Constructor to initialize config
    public KafkaSourceConfig(Map<String, String> originals) {
        super(configDef(), originals);
    }

    // Define configuration schema
    public static ConfigDef configDef() {
        return new ConfigDef()
                .define(SOURCE_BOOTSTRAP_SERVERS, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Kafka source cluster")
                .define(SOURCE_TOPIC, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Source Kafka topic")
                .define(TARGET_TOPIC, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Kafka target topic");
    }
}