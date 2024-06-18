import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.test.EmbeddedKafkaBroker;

import javax.swing.*;

@Slf4j
public class Main {
    public static void main(String[] args) {
        EmbeddedKafkaBroker kafkaBroker = new EmbeddedKafkaBroker(1)
                .kafkaPorts(9092);

        kafkaBroker.afterPropertiesSet();

        SwingUtilities.invokeLater(()->new LoginWindow("test"));
        SwingUtilities.invokeLater(()->new LoginWindow("test"));
        SwingUtilities.invokeLater(()->new LoginWindow("test"));
    }
}