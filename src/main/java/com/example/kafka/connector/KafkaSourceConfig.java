package com.example.kafka.connector;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

public class KafkaSourceConfig extends AbstractConfig {
    public static final String SOURCE_BOOTSTRAP_SERVERS = "source.bootstrap.servers";
    public static final String SOURCE_TOPIC = "source.topic";
    public static final String TARGET_TOPIC = "target.topic";

    // External Kafka Authentication Settings
    public static final String SOURCE_SECURITY_PROTOCOL = "source.security.protocol";
    public static final String SOURCE_SASL_MECHANISM = "source.sasl.mechanism";
    public static final String SOURCE_SASL_KERBEROS_SERVICE_NAME = "source.sasl.kerberos.service.name";
    public static final String SOURCE_SASL_JAAS_CONFIG = "source.sasl.jaas.config";

    public KafkaSourceConfig(Map<String, String> originals) {
        super(configDef(), originals);
    }

    public static ConfigDef configDef() {
        return new ConfigDef()
                .define(SOURCE_BOOTSTRAP_SERVERS, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Kafka source cluster")
                .define(SOURCE_TOPIC, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Source Kafka topic")
                .define(TARGET_TOPIC, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Kafka target topic")

                // External Kafka Authentication
                .define(SOURCE_SECURITY_PROTOCOL, ConfigDef.Type.STRING, "SASL_PLAINTEXT", ConfigDef.Importance.HIGH, "Security protocol for source Kafka")
                .define(SOURCE_SASL_MECHANISM, ConfigDef.Type.STRING, "GSSAPI", ConfigDef.Importance.HIGH, "SASL mechanism for source Kafka")
                .define(SOURCE_SASL_KERBEROS_SERVICE_NAME, ConfigDef.Type.STRING, "kafka", ConfigDef.Importance.HIGH, "Kerberos service name for source Kafka")
                .define(SOURCE_SASL_JAAS_CONFIG, ConfigDef.Type.STRING, "", ConfigDef.Importance.HIGH, "JAAS config for source Kafka authentication");
    }
}