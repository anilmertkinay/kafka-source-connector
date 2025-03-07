package com.example.kafka.connector;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaAndValue;

import java.time.Duration;
import java.util.*;

public class KafkaSourceTask extends SourceTask {
    private KafkaConsumer<String, String> consumer;
    private String sourceTopic;
    private String targetTopic;

    @Override
    public String version() {
        return "1.0";
    }

    @Override
    public void start(Map<String, String> props) {
        // Create Kafka consumer
        Properties kafkaProps = new Properties();
        kafkaProps.put("bootstrap.servers", props.get("source.bootstrap.servers"));
        kafkaProps.put("group.id", "kafka-source-connector-group");
        kafkaProps.put("key.deserializer", StringDeserializer.class.getName());
        kafkaProps.put("value.deserializer", StringDeserializer.class.getName());
        kafkaProps.put("auto.offset.reset", "earliest");

        consumer = new KafkaConsumer<>(kafkaProps);
        sourceTopic = props.get("source.topic");
        targetTopic = props.get("target.topic");
        consumer.subscribe(Collections.singletonList(sourceTopic));
    }

    @Override
    public List<SourceRecord> poll() {
        List<SourceRecord> recordsList = new ArrayList<>();
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
        consumer.close();
    }
}