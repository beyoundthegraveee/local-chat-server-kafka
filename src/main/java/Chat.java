import org.apache.kafka.clients.producer.ProducerRecord;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.concurrent.Executors;

public class Chat extends JFrame {
    private JPanel MainPanel;
    private JTextArea chatView;
    private JButton sendButton;
    private JTextField sendField;
    private JTextArea listOfUsers;

    private static ArrayList<String> connectedUsers = new ArrayList<>();

    private final MessageConsumer messageConsumer;



    public Chat(String id, String topic) {
        messageConsumer = new MessageConsumer(topic, id);
        listOfUsers.setEditable(false);
        setSize(700, 300);
        this.setLocationRelativeTo(null);
        setResizable(false);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.add(MainPanel);
        this.setVisible(true);
        this.setTitle("ChatView of: " + id);
        setContentPane(MainPanel);
        chatView.setEditable(false);
        connectedUsers.add(id);
        updateUsersList();

        Executors.newSingleThreadExecutor().submit(() -> {
            while (true) {
                messageConsumer.kafkaConsumer.poll(Duration.of(1, ChronoUnit.SECONDS)).forEach(x -> {
                    String message = x.value();
                    if (message.contains("logged in at")){
                        updateUsersList();
                    }
                    chatView.append(x.value() + System.lineSeparator());
                    }
                );
            }
        });

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MessageProducer.send(new ProducerRecord<>(topic, LocalDateTime.now() + " " + id + " " + sendField.getText()));
            }
        });




    }

    private void updateUsersList() {
        listOfUsers.setText("");
        for (String user : connectedUsers) {
            listOfUsers.append("- " + user + System.lineSeparator());
        }
    }
}
