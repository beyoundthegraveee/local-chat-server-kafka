import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class MessageConsumer {

    KafkaConsumer<String, String> kafkaConsumer;

    private Map<String, List<String>> map = new HashMap<>();

    private List<String> messages = new ArrayList<>();


    public MessageConsumer(String topic, String id) {

        kafkaConsumer = new KafkaConsumer<String, String>(
                Map.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
                        ConsumerConfig.GROUP_ID_CONFIG, id,
                        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName(),
                        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName(),
                        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest",
                        ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true
                )
        );

        kafkaConsumer.subscribe(Collections.singletonList(topic));
        ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.of(1, ChronoUnit.SECONDS));
        for (ConsumerRecord<String, String> record : records) {
            String message = record.value();
            messages.add(message);
        }
        map.put(id, messages);
    }

    public List<String> getMessagesForUser(String userId) {
        return map.getOrDefault(userId, new ArrayList<>());
    }



}
