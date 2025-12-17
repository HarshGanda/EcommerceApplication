package com.ecommerce.order.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class KafkaProducerConfigTest {

    @Test
    void testProducerFactoryCreation() {
        // Test: ProducerFactory is created with correct configuration
        KafkaProducerConfig config = new KafkaProducerConfig();
        ReflectionTestUtils.setField(config, "bootstrapServers", "localhost:9092");

        ProducerFactory<String, String> factory = config.producerFactory();

        assertNotNull(factory);
        assertEquals("localhost:9092",
            factory.getConfigurationProperties().get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals(StringSerializer.class,
            factory.getConfigurationProperties().get(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
        assertEquals(StringSerializer.class,
            factory.getConfigurationProperties().get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));
    }

    @Test
    void testKafkaTemplateCreation() {
        // Test: KafkaTemplate is created with producer factory
        KafkaProducerConfig config = new KafkaProducerConfig();
        ReflectionTestUtils.setField(config, "bootstrapServers", "localhost:9092");

        KafkaTemplate<String, String> kafkaTemplate = config.kafkaTemplate();

        assertNotNull(kafkaTemplate);
        assertNotNull(kafkaTemplate.getProducerFactory());
    }

    @Test
    void testProducerFactoryWithCustomBootstrapServers() {
        // Test: ProducerFactory with custom bootstrap servers
        KafkaProducerConfig config = new KafkaProducerConfig();
        ReflectionTestUtils.setField(config, "bootstrapServers", "kafka-server:9093");

        ProducerFactory<String, String> factory = config.producerFactory();

        assertEquals("kafka-server:9093",
            factory.getConfigurationProperties().get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
    }

    @Test
    void testProducerFactorySerializers() {
        // Test: Both key and value serializers are StringSerializer
        KafkaProducerConfig config = new KafkaProducerConfig();
        ReflectionTestUtils.setField(config, "bootstrapServers", "localhost:9092");

        ProducerFactory<String, String> factory = config.producerFactory();

        assertEquals(StringSerializer.class,
            factory.getConfigurationProperties().get(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
        assertEquals(StringSerializer.class,
            factory.getConfigurationProperties().get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));
    }
}

