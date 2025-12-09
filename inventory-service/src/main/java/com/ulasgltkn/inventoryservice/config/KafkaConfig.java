package com.ulasgltkn.inventoryservice.config;



import com.ulasgltkn.inventoryservice.dto.event.InventoryEvent;
import com.ulasgltkn.inventoryservice.dto.event.OrderCreatedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    // ===================== PRODUCER (InventoryEvent) =====================

    /**
     * ProducerFactory:
     * - Kafka'ya event gönderecek producer'ların configini tutar.
     * - Key: String, Value: InventoryEvent
     * - JsonSerializer ile InventoryEvent otomatik JSON'a çevrilir.
     */
    @Bean
    public ProducerFactory<String, InventoryEvent> inventoryEventProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        props.put(JacksonJsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaProducerFactory<>(props);
    }

    /**
     * KafkaTemplate:
     * - Servis içinde publishInventoryEvent() metodunda kullanıyoruz.
     * - inventory-events topiğine event basmak için tek entry point.
     */
    @Bean
    public KafkaTemplate<String, InventoryEvent> inventoryEventKafkaTemplate() {
        return new KafkaTemplate<>(inventoryEventProducerFactory());
    }

    // ===================== CONSUMER (OrderCreatedEvent) =====================

    /**
     * ConsumerFactory:
     * - ORDER_CREATED eventlerini dinleyen consumer'ın configleri.
     * - JsonDeserializer ile OrderCreatedEvent JSON'dan objeye çevrilir.
     */
    @Bean
    public ConsumerFactory<String, OrderCreatedEvent> orderCreatedConsumerFactory() {
        JacksonJsonDeserializer<OrderCreatedEvent> deserializer =
                new JacksonJsonDeserializer<>(OrderCreatedEvent.class);
        deserializer.addTrustedPackages("*");

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "inventory-service");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    /**
     * ListenerContainerFactory:
     * - @KafkaListener ile anotelenmiş metotların
     *   nasıl çalışacağını (thread sayısı, consumer factory) belirler.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent> orderCreatedKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderCreatedConsumerFactory());
        return factory;
    }
}
