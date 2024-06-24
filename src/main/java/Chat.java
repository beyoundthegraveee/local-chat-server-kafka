import org.apache.kafka.clients.producer.ProducerRecord;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Chat extends JFrame {
    private JPanel MainPanel;
    private JTextArea chatView;
    private JButton sendButton;
    private JTextField sendField;
    private JList<String> listOfUsers;

    private DateTimeFormatter formatter;

    private String formattedDate;

    private  ExecutorService service;

    private DefaultListModel<String> listModel;
    private JButton loggoutButton;

    private static ArrayList<String> connectedUsers = new ArrayList<>();


    private final MessageConsumer messageConsumer;




    public Chat(String id, String topic) {
        messageConsumer = new MessageConsumer(topic, id);
        getPreviousMessages(id);
        listModel = new DefaultListModel<>();
        listOfUsers.setModel(listModel);
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        formattedDate = LocalDateTime.now().format(formatter);
        setSize(700, 300);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        add(MainPanel);
        setVisible(true);
        setTitle("ChatView of: " + id);
        setContentPane(MainPanel);
        chatView.setEditable(false);
        connectedUsers.add(id);
        updateUsersList();


        service = Executors.newSingleThreadExecutor();
        service.submit(() -> {
            try {

                while (true) {
                    messageConsumer.kafkaConsumer.poll(Duration.of(1, ChronoUnit.SECONDS)).forEach(x -> {
                                String message = x.value();
                                if (message.contains("logged in at")) {
                                    String user = message.split(",")[0].split(":")[1].trim();
                                    if (!connectedUsers.contains(user)) {
                                        connectedUsers.add(user);
                                    }
                                    updateUsersList();
                                } else if (message.contains("logged out at")) {
                                    String user = message.split(",")[0].split(":")[1].trim();
                                    connectedUsers.remove(user);
                                    updateUsersList();
                                }
                                chatView.append(x.value() + System.lineSeparator());
                            }
                    );
                }
            } catch (Exception e){
                e.printStackTrace();
            } finally {
                messageConsumer.kafkaConsumer.close();
            }
        });

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MessageProducer.send(new ProducerRecord<>(topic, formattedDate + " USER: " + id + "- " + sendField.getText()));
                sendField.setText("");
            }
        });


        loggoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectedUsers.remove(id);
                MessageProducer.send(new ProducerRecord<>(topic, "USER: " + id + ", logged out at: " + formattedDate));
                dispose();
                SwingUtilities.invokeLater(()->new LoginWindow("test"));
            }
        });
    }

    private void updateUsersList() {
        listModel.clear();
        for (String user : connectedUsers) {
            listModel.addElement(user);
        }
    }

    private void getPreviousMessages(String id){
        List<String> messages = messageConsumer.getMessagesForUser(id);
        SwingUtilities.invokeLater(() -> {
            for (String message : messages) {
                chatView.append(message + System.lineSeparator());
            }
        });
    }
}
