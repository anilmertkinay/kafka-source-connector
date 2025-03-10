package com.example.kafka.connector;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;

import java.time.Duration;
import java.util.*;

public class KafkaSourceTask extends SourceTask {
    private KafkaConsumer<String, String> consumer;
    private String sourceTopic;
    private String targetTopic;
    private boolean running = true;

    @Override
    public String version() {
        return "1.1";
    }

    @Override
    public void start(Map<String, String> props) {
        KafkaSourceConfig config = new KafkaSourceConfig(props);

        Properties kafkaProps = new Properties();
        kafkaProps.put("bootstrap.servers", config.getString(KafkaSourceConfig.SOURCE_BOOTSTRAP_SERVERS));
        kafkaProps.put("group.id", "kafka-source-connector-group");
        kafkaProps.put("key.deserializer", StringDeserializer.class.getName());
        kafkaProps.put("value.deserializer", StringDeserializer.class.getName());
        kafkaProps.put("auto.offset.reset", "earliest");

        // External Kafka Authentication
        kafkaProps.put("security.protocol", config.getString(KafkaSourceConfig.SOURCE_SECURITY_PROTOCOL));
        kafkaProps.put("sasl.mechanism", config.getString(KafkaSourceConfig.SOURCE_SASL_MECHANISM));
        kafkaProps.put("sasl.kerberos.service.name", config.getString(KafkaSourceConfig.SOURCE_SASL_KERBEROS_SERVICE_NAME));

        String jaasConfig = config.getString(KafkaSourceConfig.SOURCE_SASL_JAAS_CONFIG);
        if (!jaasConfig.isEmpty()) {
            kafkaProps.put("sasl.jaas.config", jaasConfig);
        }

        consumer = new KafkaConsumer<>(kafkaProps);
        sourceTopic = config.getString(KafkaSourceConfig.SOURCE_TOPIC);
        targetTopic = config.getString(KafkaSourceConfig.TARGET_TOPIC);
        consumer.subscribe(Collections.singletonList(sourceTopic));
    }

    @Override
    public List<SourceRecord> poll() {
        List<SourceRecord> recordsList = new ArrayList<>();
        if (!running) return recordsList;

        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
        for (ConsumerRecord<String, String> record : records) {
            Map<String, ?> sourcePartition = Collections.singletonMap("topic", sourceTopic);
            Map<String, ?> sourceOffset = Collections.singletonMap("offset", record.offset());

            recordsList.add(new SourceRecord(
                    sourcePartition,
                    sourceOffset,
                    targetTopic,
                    Schema.STRING_SCHEMA,
                    record.key(),
                    Schema.STRING_SCHEMA,
                    record.value()
            ));
        }
        return recordsList;
    }

    @Override
    public void stop() {
        running = false;
        if (consumer != null) {
            consumer.wakeup(); // Ensures poll() exits if waiting
            consumer.close();
        }
    }
}