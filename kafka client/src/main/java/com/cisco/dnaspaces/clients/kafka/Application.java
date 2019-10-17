package com.cisco.dnaspaces.clients.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Collections;
import java.util.Properties;
import java.util.Scanner;

public class Application {

    private static final Scanner in = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        runConsumer();
    }

    private static String readInput(String message, String defaultValue) {
        System.out.print(message);
        String inputValue = in.nextLine();
        if (inputValue == null || inputValue.isEmpty())
            return defaultValue;
        return inputValue;
    }

    private static Consumer<String, String> createConsumer() {
        final Properties props = new Properties();
        String bootstrapServerConfig = readInput("Please enter Bootstrap Servers config(default value is localhost:9092) : ", "localhost:9092");
        String groupIdConfig = readInput("Please enter Group Id config(default value is KafkaExampleConsumer) : ", "KafkaExampleConsumer");
        String keyDeSerializerConfig = readInput("Please enter Key DeSerializer config(default value is org.apache.kafka.common.serialization.StringDeserializer) : ", "org.apache.kafka.common.serialization.StringDeserializer");
        String valueDeSerializerConfig = readInput("Please enter Value DeSerializer config(default value is org.apache.kafka.common.serialization.StringDeserializer) : ", "org.apache.kafka.common.serialization.StringDeserializer");
        String topicName = readInput("Please enter Topic Name to be subscribed for(default value is DEVICE_LOCATION_UPDATE) : ", "DEVICE_LOCATION_UPDATE");


        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServerConfig);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupIdConfig);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeSerializerConfig);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeSerializerConfig);
        // Create the consumer using props.
        final Consumer<String, String> consumer = new KafkaConsumer<String, String>(props);
        // Subscribe to the topic.
        consumer.subscribe(Collections.singletonList(topicName));
        return consumer;
    }

    static void runConsumer() throws InterruptedException {
        final Consumer<String, String> consumer = createConsumer();
        final int giveUp = 100;
        int noRecordsCount = 0;
        while (true) {
            final ConsumerRecords<String, String> consumerRecords =
                    consumer.poll(1000);
            if (consumerRecords.count() == 0) {
                noRecordsCount++;
                if (noRecordsCount > giveUp) break;
                else continue;
            }
            consumerRecords.forEach(record -> {
                System.out.printf("Consumer Record:(%s, %s, %d, %d)\n",
                        record.key(), record.value(),
                        record.partition(), record.offset());
            });
            consumer.commitAsync();
        }
        consumer.close();
        System.out.println("DONE");
    }
}
